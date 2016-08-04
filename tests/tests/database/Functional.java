package tests.database;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import rsa.RSA;
import sha.SHA;
import utilities.Utilities;
import db.Database;
import db.Database.Status;
import db.SpecialPassword;
import logger.Logger;
import main.Exceptions;
import main.Exceptions.XC;
import main.JfxRunner;
import main.Properties;
import main.TestUtilities;

@RunWith(JfxRunner.class)
public class Functional
{
    /**
     * cipherText is written to file and added to Database on construction
     * Suppose that Database constructor works correctly
     */
    private final static class testPwd
    {
        private final static String masterPassword = "12345";

        public final static String  cipherText     =
            "000661162055378452059166047568408511888578115461734245685557172043790183050294770866848296551952945411687998966643812980932107263560997083194173776982691610417838368495045583308992062915036063277420648945364840884160539937412227385219482687942930971346166301562186789538808790941858760716143964613421194365289600038396652875045618758526888338316747230797120922786007669897399569369906654101104782749622537434592461233103514691468855317382032207239712525881208853260755779175533347074376365100768240893007238012760274206726907045188911581696451140416768973735243777118595436533034235377191280865417884850119191629119002340008776621953599269430121861752762460562996174749493976914800027603345291120882319576355714163256459020267929219414048017461276721570966491935152828139213140973078155684722767112347818216391896198996707507566583359757862477261875390150374547347296496964304543209461383511078964384980607200574330538115332441235000890836463643030730600194654871198822983373649026962183633885778188764244629398073613610135886027031388163305224691672785242600519701868435922000339936196782687634440734378954588200401304643283333799348792851050170459611915016664469766604419431213191515934954313390527423662665812900771868365849397499558067400064838366643297671975245972648694677702073095039937100612473208477852323989944332637775011716430404082208069315986138180687793963096728014636341722222203940686023889898569821770411547361699275311879438468613596151473832879820932659876443011279342597867076513476035549710757544640334136092771232136526683925990001060860290453279728227214179390700118332097660164690660008165753555745901000454587054018904963904580459566558768365878720842425842311962676646565844000235763707765991815804454895291864591021303687059699554196047443027013293006951923689265818228078976365231752071961030850293972555727235937175612644125147071";

        public final static String  name           = "testPassword";
        public final static String  shortcut       = "1";
        public final static String  comment        = "testComment";
        public final static String  url            = "testUrl";
        public final static int     length         = 32;
        public final static int     shaCycles      = 185;
    };

    private final String SALT_P     = "P";
    private final String SALT_Q     = "Q";
    private final String SALT_E     = "E";

    private Database     db         = null;
    private RSA          rsa        = null;
    private byte[]       masterHash = null;

    private String       fileName   = null;
    private File         vaultFile  = null;

    static AtomicInteger num        = new AtomicInteger(0);

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
        masterHash = SHA.getHashBytes(testPwd.masterPassword.getBytes());
        try
        {
            rsa = new RSA(SHA.getHashString(masterHash, SALT_P), SHA.getHashString(masterHash, SALT_Q),
                SHA.getHashString(masterHash, SALT_E));

            fileName = "testFile" + num.incrementAndGet();

            vaultFile = new File(Properties.PATHS.VAULT + fileName + Properties.EXTENSIONS.VAULT);
            if (vaultFile.exists()) vaultFile.delete();

            Utilities.writeToFile(vaultFile.getAbsolutePath(), testPwd.cipherText);
            db = new Database(rsa, fileName, false, null);
        }
        catch (Exceptions e)
        {
            throw new Exception("pi4alka");
        }
    }

    @After
    public void tearDown() throws Exception
    {
        vaultFile.delete();
        vaultFile = null;
        rsa = null;
        fileName = null;
    }

    private CompletableFuture<Status> attachDbSync(CompletableFuture<Status> future)
    {
        db.getStatusProperty().addListener((o, ov, nv) ->
        {
            Logger.printDebug(ov.toString() + " -> " + nv.toString());
            if (nv == Status.SYNCHRONIZED) future.complete(nv);
        });

        return future;
    }

    private void waitDbSync(CompletableFuture<Status> future)
    {
        try
        {
            future.get(Properties.DATABASE.MAX_RETRIES * Properties.DATABASE.SYNC_RETRY_DELAY_MS,
                TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException | ExecutionException | TimeoutException e)
        {
            fail("Didn't get the result: " + e.getMessage());
        }
    }

    private Stream<SpecialPassword> streamPwdFile()
    {
        try
        {
            return Files.lines(Paths.get(vaultFile.getAbsolutePath())).map(cipher -> rsa.decrypt(cipher))
                .map(bytes -> TestUtilities.bytesToObject(bytes))
                .map(object -> new SpecialPassword((HashMap<String, String>) object, null));
        }
        catch (IOException e)
        {
            fail("Failed to stream file: " + e.getMessage());
        }
        return null;
    }

    @Test
    public void testAddEntrySuccess()
    {
        SpecialPassword pwd = null;

        final String name = "Some password";
        final String comment = "Another comment";
        final String url = "url://asdf.fdsa.ru";
        final String shortcut = "4";
        final int length = 32;
        final int shaCycles = 56;

        try
        {
            HashMap<String, String> pwdMap = new HashMap<String, String>();
            pwdMap.put(MockSpecialPassword.getMapKeyName(), name);
            pwdMap.put(MockSpecialPassword.getMapKeyComment(), comment);
            pwdMap.put(MockSpecialPassword.getMapKeyLength(), Integer.toString(length));
            pwdMap.put(MockSpecialPassword.getMapKeyShaCycles(), Integer.toString(shaCycles));
            pwdMap.put(MockSpecialPassword.getMapKeyUrl(), url);
            pwdMap.put(MockSpecialPassword.getMapKeyShortcut(), shortcut);
            pwd = new SpecialPassword(pwdMap, null);

            CompletableFuture<Status> future = attachDbSync(new CompletableFuture<Status>());
            db.addEntry(pwd);
            waitDbSync(future);
        }
        catch (Exceptions e)
        {
            fail("Can't add special password to database: " + e.getCode().toString());
        }

        // verify that SpecialPassword is added to vault file
        assertTrue(streamPwdFile()
            .anyMatch(sp -> MockSpecialPassword.cmpAllFields(sp, name, comment, url, length, shaCycles)));

        // verify that added password is returned in getDecrypted
        assertTrue(db.getDecrypted().stream()
            .anyMatch(sp -> MockSpecialPassword.cmpAllFields(sp, name, comment, url, length, shaCycles)));
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
        SpecialPassword pwd = null;
        HashMap<String, String> pwdMap = new HashMap<String, String>();
        pwdMap.put(MockSpecialPassword.getMapKeyName(), testPwd.name);
        pwdMap.put(MockSpecialPassword.getMapKeyComment(), comment);
        pwdMap.put(MockSpecialPassword.getMapKeyLength(), length);
        pwdMap.put(MockSpecialPassword.getMapKeyShaCycles(), shaCycles);
        pwdMap.put(MockSpecialPassword.getMapKeyUrl(), url);
        pwdMap.put(MockSpecialPassword.getMapKeyShortcut(), shortcut);
        pwd = new SpecialPassword(pwdMap, null);

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
            assertTrue(streamPwdFile()
                .noneMatch(sp -> MockSpecialPassword.cmpAllFields(sp, null, comment, url, length, shaCycles)));
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
        SpecialPassword pwd = null;
        HashMap<String, String> pwdMap = new HashMap<String, String>();
        pwdMap.put(MockSpecialPassword.getMapKeyName(), name);
        pwdMap.put(MockSpecialPassword.getMapKeyComment(), comment);
        pwdMap.put(MockSpecialPassword.getMapKeyLength(), length);
        pwdMap.put(MockSpecialPassword.getMapKeyShaCycles(), shaCycles);
        pwdMap.put(MockSpecialPassword.getMapKeyUrl(), url);
        pwdMap.put(MockSpecialPassword.getMapKeyShortcut(), testPwd.shortcut);
        pwd = new SpecialPassword(pwdMap, null);

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
            assertTrue(MockSpecialPassword.cmpAllFields(((SpecialPassword) e.getObject()), testPwd.name,
                testPwd.comment, testPwd.url, testPwd.length, testPwd.shaCycles));

            // and password not added (overwrite or add)
            assertEquals(Status.SYNCHRONIZED, db.getStatus());
            assertTrue(streamPwdFile()
                .noneMatch(sp -> MockSpecialPassword.cmpAllFields(sp, name, comment, url, length, shaCycles)));
        }
    }

    //
    // @Test
    // public void testDeleteEntrySuccess()
    // {
    // // delete
    // // verify that file no longer contains entry
    // // and getDecrypted() does not return pwd
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public void testDeleteEntryFailNotFound()
    // {
    // // delete
    // // verify that exception is thrown
    // // no passwords deleted
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public void testReplaceEntrySuccess()
    // {
    // // replace
    // // verify that old password does not exist
    // // new password present
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public void testReplaceEntryFailNotFound()
    // {
    // // replace
    // // verify exception
    // // verify no changes to db
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public void testReplaceEntryFailNewNameTaken()
    // {
    // // replace
    // // verify exception
    // // verify no changes to db
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public void testReplaceEntryFailShortcutTaken()
    // {
    // // replace
    // // verify exception
    // // verify no changes to db
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // public void testGetSetName()
    // {
    // // set name
    // // verify that name entry is added to file
    // // get name equals to set name
    // fail("Not yet implemented");
    // }

    // @Test
    // public void testCantChangeDatabaseContents()
    // {
    // // retrieve SpecialPassword to db
    // // change it
    // // retrieve same SpeialPasswod from db
    // // verify that changes made in first SP is not reflected in second
    // }
}
