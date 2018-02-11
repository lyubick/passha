package org.kgbt.passha.tests.database;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.kgbt.passha.core.rsa.RSA;
import org.kgbt.passha.core.sha.SHA;
import org.kgbt.passha.core.common.Utilities;
import org.kgbt.passha.core.db.Database;
import org.kgbt.passha.core.db.Database.Status;
import org.kgbt.passha.core.db.SpecialPassword;
import org.kgbt.passha.core.logger.Logger;
import org.kgbt.passha.core.common.Exceptions;
import org.kgbt.passha.core.common.Exceptions.XC;
import org.kgbt.passha.core.common.cfg.Properties;
import org.kgbt.passha.main.TestUtilities;
import org.kgbt.passha.reflections.DatabaseReflection;
import org.kgbt.passha.reflections.SpecialPasswordReflection;

public class DatabaseFunctional
{
    private final static class DatabaseSetUp
    {
        private final static String masterPassword = "12345";

        private final static String SALT_P = "P";
        private final static String SALT_Q = "Q";
        private final static String SALT_E = "E";

        private final static class TestPwd
        {
            public final String cipherText;
            public final String name;
            public final String comment;
            public final String url;
            public final String shortcut;
            public final int    length;
            public final int    shaCycles;

            private TestPwd(String cipherText, String name, String comment, String url, String shortcut, int length,
                int shaCycles)
            {
                this.cipherText = cipherText;
                this.name = name;
                this.shortcut = shortcut;
                this.comment = comment;
                this.url = url;
                this.length = length;
                this.shaCycles = shaCycles;
            }
        }

        private static TestPwd[] presetPwds = { new TestPwd(
            "000661162055378452059166047568408511888578115461734245685557172043790183050294770866848296551952945411687998966643812980932107263560997083194173776982691610417838368495045583308992062915036063277420648945364840884160539937412227385219482687942930971346166301562186789538808790941858760716143964613421194365289600038396652875045618758526888338316747230797120922786007669897399569369906654101104782749622537434592461233103514691468855317382032207239712525881208853260755779175533347074376365100768240893007238012760274206726907045188911581696451140416768973735243777118595436533034235377191280865417884850119191629119002340008776621953599269430121861752762460562996174749493976914800027603345291120882319576355714163256459020267929219414048017461276721570966491935152828139213140973078155684722767112347818216391896198996707507566583359757862477261875390150374547347296496964304543209461383511078964384980607200574330538115332441235000890836463643030730600194654871198822983373649026962183633885778188764244629398073613610135886027031388163305224691672785242600519701868435922000339936196782687634440734378954588200401304643283333799348792851050170459611915016664469766604419431213191515934954313390527423662665812900771868365849397499558067400064838366643297671975245972648694677702073095039937100612473208477852323989944332637775011716430404082208069315986138180687793963096728014636341722222203940686023889898569821770411547361699275311879438468613596151473832879820932659876443011279342597867076513476035549710757544640334136092771232136526683925990001060860290453279728227214179390700118332097660164690660008165753555745901000454587054018904963904580459566558768365878720842425842311962676646565844000235763707765991815804454895291864591021303687059699554196047443027013293006951923689265818228078976365231752071961030850293972555727235937175612644125147071",
            "testPassword", "testComment", "testUrl", "1", 32, 185), new TestPwd(
            "000886041694640044936277628825085123697093577479679132765051057822335113153336064859606361661172283460873419402384066186034219049419403777594828275079670964661490107550307949222944402670518576112538661874173687957533533608296656504536738069053340243742142976279080847101528596523932981074956229698518952093919100052503946805894336779007603633678537789134775617481813730200267535769664763671052615511379176449858490097227816187363637570193915123135173813619563269243670185871223363856378638426703232661848193491020874964651819030075642851384644141389369072531033886816465885838328132113950920975375610969718429981894064320009457368122170489851869178378455606233034493510900283658567301298835250567855624500225536458873194703385503524709319176483168157565175042660077248365454974610119661157871053388744895040427305748627741889055305566996193057865487382053767559721484473802419254501807313899500520134076063890099982970901012937160000086219672976590078096561540249742025776007838716469831512927389442318773615962931656009735123019537426443164599468897363441402680285010935894010149805929394042594255958036027923800257520825115959927265603502575627265268612901813432051593242004839109499253575614722666831571339796222868434619315544889818081400013803615500246569560895405281218516874573570850754288399793562111802355047708237237218710237664119610485904371269274975371906354837241449716316822953948053568743886024717586333623144936427606360730951304440930328851211873176483209048116955107926485906515161687692426937902081986338139333160418259109531299950006804855239550692796449913626894851405097684576406489843914076145334478006410907708771213694050360173445178450397002952962514672938663227130412674815667221852969791035086467048826675708807518760271003102771760329562520025569551290048027224719314157522526359954327832156658708224344552611547081088498174510168",
            "secondTestPassword", "second test comment", "http://goturussia.ru", "", 16, 0) };
    }

    SpecialPasswordReflection specialPasswordReflection = SpecialPasswordReflection.getInstance();

    private Database db         = null;
    private RSA      rsa        = null;
    private byte[]   masterHash = null;

    private String fileName  = null;
    private File   vaultFile = null;

    static AtomicInteger num = new AtomicInteger(0);

    @Before
    public void setUp() throws Exception
    {
        try
        {
            Logger.loggerON("");
        }
        catch (Exceptions e1)
        {
            System.out.println("Failed to turn on logger");
        }
        masterHash = SHA.getHashBytes(DatabaseSetUp.masterPassword.getBytes());
        try
        {
            rsa = new RSA(SHA.getHashString(masterHash, DatabaseSetUp.SALT_P),
                SHA.getHashString(masterHash, DatabaseSetUp.SALT_Q),
                SHA.getHashString(masterHash, DatabaseSetUp.SALT_E));

            fileName = "testFunctionalFile" + num.incrementAndGet();

            vaultFile = new File(Properties.PATHS.VAULT + fileName + Properties.EXTENSIONS.VAULT);
            if (vaultFile.exists())
                vaultFile.delete();

            Utilities.writeToFile(vaultFile.getAbsolutePath(), DatabaseSetUp.presetPwds[0].cipherText,
                DatabaseSetUp.presetPwds[1].cipherText);
            db = new Database(rsa, fileName, false, null);
        }
        catch (Exceptions e)
        {
            throw new Exception("pi4alka: " + e.getCode().toString());
        }
    }

    @After
    public void tearDown()
    {
        vaultFile.delete();
        vaultFile = null;
        rsa = null;
        fileName = null;
        db = null;
        masterHash = null;

        try
        {
            Logger.getInstance().loggerOFF();
        }
        catch (Exceptions e)
        {
            System.out.println("Can't close logger");
        }
    }

    private CompletableFuture<Status> attachDbSync(CompletableFuture<Status> future)
    {
        db.setOnStatusChanged(nv -> {
            Logger.printDebug("DB status changed to " + nv.toString());
            if (nv == Status.SYNCHRONIZED)
                future.complete(nv);
        });

        return future;
    }

    private void waitDbSync(CompletableFuture<Status> future)
    {
        try
        {
            future
                .get(Properties.DATABASE.MAX_RETRIES * Properties.DATABASE.SYNC_RETRY_DELAY_MS, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException | ExecutionException | TimeoutException e)
        {
            fail("Didn't get the result: " + e.getMessage());
        }
    }

    private Stream<HashMap<String, String>> streamMapsFromDbFile()
    {
        try
        {
            return Files.lines(Paths.get(vaultFile.getAbsolutePath())).map(cipher -> rsa.decrypt(cipher))
                .map(bytes -> (HashMap<String, String>) TestUtilities.bytesToObject(bytes));
        }
        catch (IOException e)
        {
            fail("Failed to stream file: " + e.getMessage());
        }
        return Stream.empty();
    }

    private Stream<SpecialPassword> streamPwdsFromDbFile()
    {
        return streamMapsFromDbFile().map(object -> new SpecialPassword(object, null));
    }

    private SpecialPassword createSpecialPassword(DatabaseSetUp.TestPwd predefinedPwd)
    {
        return createSpecialPassword(predefinedPwd.name, predefinedPwd.comment, Integer.toString(predefinedPwd.length),
            Integer.toString(predefinedPwd.shaCycles), predefinedPwd.url, predefinedPwd.shortcut);
    }

    private SpecialPassword createSpecialPassword(String name, String comment, String length, String shaCycles,
        String url, String shortcut)
    {
        HashMap<String, String> pwdMap = new HashMap<>();

        try
        {
            pwdMap.put(specialPasswordReflection.NAME().get(null).toString(), name);
            pwdMap.put(specialPasswordReflection.COMMENT().get(null).toString(), comment);
            pwdMap.put(specialPasswordReflection.LENGTH().get(null).toString(), length);
            pwdMap.put(specialPasswordReflection.SHA_CYCLES().get(null).toString(), shaCycles);
            pwdMap.put(specialPasswordReflection.URL().get(null).toString(), url);
            pwdMap.put(specialPasswordReflection.SHORTCUT().get(null).toString(), shortcut);
        }
        catch (IllegalArgumentException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        return new SpecialPassword(pwdMap, null);
    }

    @Test
    public void testAddEntrySuccess()
    {
        final String name = "Some password";
        final String comment = "Another comment";
        final String url = "url://asdf.fdsa.ru";
        final String shortcut = "4";
        final int length = 32;
        final int shaCycles = 56;

        SpecialPassword pwd =
            createSpecialPassword(name, comment, Integer.toString(length), Integer.toString(shaCycles), url, shortcut);

        try
        {
            CompletableFuture<Status> future = attachDbSync(new CompletableFuture<>());
            db.addEntry(pwd);
            waitDbSync(future);
        }
        catch (Exceptions e)
        {
            fail("Can't add special password to database: " + e.getCode().toString());
        }

        // verify that SpecialPassword is added to vault file
        assertTrue(streamPwdsFromDbFile().anyMatch(sp -> TestUtilities.SpecialPassword.cmpAllFields(sp, pwd)));

        // verify that added password is returned in getDecrypted
        assertTrue(db.getDecrypted().stream().anyMatch(sp -> TestUtilities.SpecialPassword.cmpAllFields(sp, pwd)));
    }

    @Test
    public void testAddEntryFailNameTaken()
    {
        final String comment = "comment does not matter";
        final String length = "8";
        final String shaCycles = "65";
        final String url = "some url";
        final String shortcut = "";
        // add entry
        SpecialPassword pwd =
            createSpecialPassword(DatabaseSetUp.presetPwds[0].name, comment, length, shaCycles, url, shortcut);

        try
        {
            db.addEntry(pwd);
            // verify that exception is thrown
            fail("Can't add password with existing name");
        }
        catch (Exceptions e)
        {
            assertEquals(XC.PASSWORD_NAME_EXISTS, e.getCode());
            assertEquals(Status.SYNCHRONIZED, db.getStatus());
            // and password not added (overwrite or add)
            assertTrue(streamPwdsFromDbFile().noneMatch(
                sp -> TestUtilities.SpecialPassword.cmpAllFields(sp, null, comment, url, length, shaCycles)));
        }
    }

    @Test
    public void testAddEntryFailShortcutTaken()
    {
        final String name = "Some unique name";
        final String comment = "comment does not matter";
        final String length = "8";
        final String shaCycles = "65";
        final String url = "some url";
        // add entry
        SpecialPassword pwd =
            createSpecialPassword(name, comment, length, shaCycles, url, DatabaseSetUp.presetPwds[0].shortcut);

        try
        {
            db.addEntry(pwd);
            // verify that exception is thrown
            fail("Can't add password with existing name");
        }
        catch (Exceptions e)
        {
            assertEquals(XC.PASSWORD_SHORTCUT_IN_USE, e.getCode());

            // verify that conflicting password is returned
            assertTrue(TestUtilities.SpecialPassword
                .cmpAllFields(((SpecialPassword) e.getObject()), DatabaseSetUp.presetPwds[0].name,
                    DatabaseSetUp.presetPwds[0].comment, DatabaseSetUp.presetPwds[0].url,
                    DatabaseSetUp.presetPwds[0].length, DatabaseSetUp.presetPwds[0].shaCycles));

            // and password not added (overwrite or add)
            assertEquals(Status.SYNCHRONIZED, db.getStatus());
            assertTrue(streamPwdsFromDbFile().noneMatch(
                sp -> TestUtilities.SpecialPassword.cmpAllFields(sp, name, comment, url, length, shaCycles)));

            assertTrue(db.getDecrypted().stream().noneMatch(
                sp -> TestUtilities.SpecialPassword.cmpAllFields(sp, name, comment, url, length, shaCycles)));
        }
    }

    @Test
    public void testDeleteEntrySuccess()
    {
        SpecialPassword pwd = createSpecialPassword(DatabaseSetUp.presetPwds[0]);

        // delete
        try
        {
            CompletableFuture<Status> future = attachDbSync(new CompletableFuture<>());
            db.deleteEntry(pwd);
            waitDbSync(future);

            // verify that file no longer contains entry
            assertTrue(streamPwdsFromDbFile().noneMatch(sp -> TestUtilities.SpecialPassword.cmpAllFields(sp, pwd)));

            // and getDecrypted() does not return pwd
            assertTrue(db.getDecrypted().stream().noneMatch(sp -> TestUtilities.SpecialPassword.cmpAllFields(sp, pwd)));
        }
        catch (Exceptions e)
        {
            fail(e.getCode().toString());
        }
    }

    @Test
    public void testDeleteEntryFailNotFound()
    {
        final String name = "Some password";
        final String comment = "Another comment";
        final String url = "url://asdf.fdsa.ru";
        final String shortcut = "4";
        final int length = 32;
        final int shaCycles = 56;

        SpecialPassword pwd =
            createSpecialPassword(name, comment, Integer.toString(length), Integer.toString(shaCycles), url, shortcut);
        // delete
        try
        {
            db.deleteEntry(pwd);
            fail("Expected exception");
        }
        catch (Exceptions e)
        {
            // verify that exception is thrown
            assertEquals(XC.PASSWORD_NOT_FOUND, e.getCode());

            assertEquals(Status.SYNCHRONIZED, db.getStatus());

            // verify that file contains entry
            assertTrue(streamPwdsFromDbFile().anyMatch(sp -> TestUtilities.SpecialPassword
                .cmpAllFields(sp, DatabaseSetUp.presetPwds[0].name, DatabaseSetUp.presetPwds[0].comment,
                    DatabaseSetUp.presetPwds[0].url, DatabaseSetUp.presetPwds[0].length,
                    DatabaseSetUp.presetPwds[0].shaCycles)));

            // and getDecrypted() return pwd
            assertTrue(db.getDecrypted().stream().anyMatch(sp -> TestUtilities.SpecialPassword
                .cmpAllFields(sp, DatabaseSetUp.presetPwds[0].name, DatabaseSetUp.presetPwds[0].comment,
                    DatabaseSetUp.presetPwds[0].url, DatabaseSetUp.presetPwds[0].length,
                    DatabaseSetUp.presetPwds[0].shaCycles)));
        }
    }

    @Test
    public void testReplaceEntrySuccess()
    {
        SpecialPassword pwd = createSpecialPassword(DatabaseSetUp.presetPwds[0]);

        final String newComment = "Dat comment!";
        final String newUrl = "www.pornhub.com";
        final String newShortcut = "f";

        // replace
        try
        {
            SpecialPassword newPwd = new SpecialPassword(pwd);
            newPwd.setAllOptionalFields(newComment, newUrl, newShortcut);
            CompletableFuture<Status> future = attachDbSync(new CompletableFuture<>());
            db.replaceEntry(newPwd, pwd);
            waitDbSync(future);

            // verify that file no longer contains old password
            assertTrue(streamPwdsFromDbFile().noneMatch(sp -> TestUtilities.SpecialPassword.cmpAllFields(sp, pwd)));

            // and getDecrypted() does not return old pwd
            assertTrue(db.getDecrypted().stream().noneMatch(sp -> TestUtilities.SpecialPassword.cmpAllFields(sp, pwd)));

            // verify that file contains new password
            assertTrue(streamPwdsFromDbFile().anyMatch(sp -> TestUtilities.SpecialPassword.cmpAllFields(sp, newPwd)));

            // and getDecrypted() return new pwd
            assertTrue(
                db.getDecrypted().stream().anyMatch(sp -> TestUtilities.SpecialPassword.cmpAllFields(sp, newPwd)));
        }
        catch (Exceptions e)
        {
            fail(e.getCode().toString() + "pwd: " + ((SpecialPassword) e.getObject()).getComment());
        }
    }

    @Test
    public void testReplaceEntryFailNotFound()
    {
        final String name = "Some password";
        final String comment = "Another comment";
        final String url = "url://asdf.fdsa.ru";
        final String shortcut = "4";
        final int length = 32;
        final int shaCycles = 56;

        SpecialPassword pwd =
            createSpecialPassword(name, comment, Integer.toString(length), Integer.toString(shaCycles), url, shortcut);

        final String newComment = "Dat comment!";
        final String newUrl = "www.pornhub.com";
        final String newShortcut = "f";

        Vector<SpecialPassword> fileDecripted = streamPwdsFromDbFile().collect(Collectors.toCollection(Vector::new));
        Vector<SpecialPassword> dbDecripted = db.getDecrypted();

        // replace
        try
        {
            SpecialPassword newPwd = new SpecialPassword(pwd);
            newPwd.setAllOptionalFields(newComment, newUrl, newShortcut);
            db.replaceEntry(newPwd, pwd);
            fail("Expected exception");
        }
        catch (Exceptions e)
        {
            // verify exception
            assertEquals(XC.PASSWORD_NOT_FOUND, e.getCode());

            // verify no changes to db
            assertEquals(Status.SYNCHRONIZED, db.getStatus());

            assertEquals(dbDecripted.size(), db.getDecrypted().size());
            assertEquals(fileDecripted.size(), streamPwdsFromDbFile().count());

            assertTrue(streamPwdsFromDbFile().allMatch(
                sp -> fileDecripted.stream().anyMatch(sp2 -> TestUtilities.SpecialPassword.cmpAllFields(sp, sp2))));

            assertTrue(db.getDecrypted().stream().allMatch(
                sp -> dbDecripted.stream().anyMatch(sp2 -> TestUtilities.SpecialPassword.cmpAllFields(sp, sp2))));
        }
    }

    @Test
    public void testReplaceEntryFailNewNameTaken()
    {
        SpecialPassword pwd = createSpecialPassword(DatabaseSetUp.presetPwds[0]);

        final String newName = DatabaseSetUp.presetPwds[1].name;
        final String newComment = "Dat comment!";
        final String newUrl = "www.pornhub.com";
        final String newShortcut = "f";

        Vector<SpecialPassword> fileDecripted = streamPwdsFromDbFile().collect(Collectors.toCollection(Vector::new));
        Vector<SpecialPassword> dbDecripted = db.getDecrypted();

        // replace
        try
        {
            SpecialPassword newPwd = new SpecialPassword(pwd);
            newPwd.setName(newName);
            newPwd.setAllOptionalFields(newComment, newUrl, newShortcut);
            db.replaceEntry(newPwd, pwd);
            fail("Expected exception");
        }
        catch (Exceptions e)
        {
            // verify exception
            assertEquals(XC.PASSWORD_NAME_EXISTS, e.getCode());

            // verify no changes to db
            assertEquals(Status.SYNCHRONIZED, db.getStatus());

            assertEquals(dbDecripted.size(), db.getDecrypted().size());
            assertEquals(fileDecripted.size(), streamPwdsFromDbFile().count());

            assertTrue(streamPwdsFromDbFile().allMatch(
                sp -> fileDecripted.stream().anyMatch(sp2 -> TestUtilities.SpecialPassword.cmpAllFields(sp, sp2))));

            assertTrue(db.getDecrypted().stream().allMatch(
                sp -> dbDecripted.stream().anyMatch(sp2 -> TestUtilities.SpecialPassword.cmpAllFields(sp, sp2))));
        }
    }

    @Test
    public void testReplaceEntryFailShortcutTaken()
    {
        SpecialPassword pwd = createSpecialPassword(DatabaseSetUp.presetPwds[1]);

        final String newComment = "Dat comment!";
        final String newUrl = "www.pornhub.com";
        final String newShortcut = DatabaseSetUp.presetPwds[0].shortcut;

        Vector<SpecialPassword> fileDecripted = streamPwdsFromDbFile().collect(Collectors.toCollection(Vector::new));
        Vector<SpecialPassword> dbDecripted = db.getDecrypted();

        // replace
        try
        {
            SpecialPassword newPwd = new SpecialPassword(pwd);
            newPwd.setAllOptionalFields(newComment, newUrl, newShortcut);
            db.replaceEntry(newPwd, pwd);
            fail("Expected exception");
        }
        catch (Exceptions e)
        {
            // verify exception
            assertEquals(XC.PASSWORD_SHORTCUT_IN_USE, e.getCode());

            // verify no changes to db
            assertEquals(Status.SYNCHRONIZED, db.getStatus());

            assertEquals(dbDecripted.size(), db.getDecrypted().size());
            assertEquals(fileDecripted.size(), streamPwdsFromDbFile().count());

            assertTrue(streamPwdsFromDbFile().allMatch(
                sp -> fileDecripted.stream().anyMatch(sp2 -> TestUtilities.SpecialPassword.cmpAllFields(sp, sp2))));

            assertTrue(db.getDecrypted().stream().allMatch(
                sp -> dbDecripted.stream().anyMatch(sp2 -> TestUtilities.SpecialPassword.cmpAllFields(sp, sp2))));
        }
    }

    @Test
    public void testGetSetName()
    {
        final String name = "somename";

        // set name
        CompletableFuture<Status> future = attachDbSync(new CompletableFuture<>());
        db.setName(name);
        waitDbSync(future);

        try
        {
            String vaultNameKey = (String) DatabaseReflection.getInstance().VAULT_NAME_KEY().get(null);
            // verify that name entry is added to file
            assertTrue(
                streamMapsFromDbFile().filter(map -> map.containsKey(vaultNameKey)).map(map -> map.get(vaultNameKey))
                    .allMatch(n -> n.equals(name)));

            // get name equals to set name
            assertEquals(name, db.getName());
        }
        catch (IllegalArgumentException | IllegalAccessException e)
        {
            fail(e.toString());
        }

    }

    @Test
    public void testCantChangeDatabaseContents()
    {
        Vector<SpecialPassword> v1 = db.getDecrypted();
        Vector<SpecialPassword> v2 = db.getDecrypted();

        // verify that we receive new objects each time
        assertTrue(v1.stream().allMatch(sp -> v2.stream().noneMatch(sp2 -> sp == sp2)));
    }
}
