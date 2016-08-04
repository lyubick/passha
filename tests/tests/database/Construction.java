package tests.database;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import db.Database;
import db.Database.Status;
import db.SpecialPassword;
import logger.Logger;
import main.Exceptions;
import main.Exceptions.XC;
import main.JfxRunner;
import main.Properties;
import rsa.RSA;
import sha.SHA;
import utilities.Utilities;

@RunWith(JfxRunner.class)
public class Construction
{
    private final String masterPassword = "12345";
    private final String SALT_P         = "P";
    private final String SALT_Q         = "Q";
    private final String SALT_E         = "E";

    private final class vaultNameTestData
    {
        public final static String cipherText =
            "001060616102850965610151182486116164069858145927784302629104655566729598229830231226084954765437796706168495325033564511920731057382608843063455493502524536953184836695302835745957769648952203937380299403768803022342683446354100703747847341033338640922065669385205215234848259397697115163796838179989662120884200006424701924885388464568051438265926375621204552524621058697465845408742163012107922487621823224196746427048222005395288283484541282616177790759184860370825881515588768229105290650364957802732935963881685771952163107177994253479371251531535601506479458318671124737815250984219270389340590752405840677568186680009481238632733729994090069880005961869926202712430363128701674569696489927056357995456235444011382266007919115067216809485208254527379990113712298493500799196706106759010707512202086824220806918256839668910838589684474129749187832839212803126836937011199989886967977184470742409379783915972242015750605142547";
        public final static String vaultName  = "testName";
    };

    private final class passwordTestData
    {
        public final static String cipherText =
            "000661162055378452059166047568408511888578115461734245685557172043790183050294770866848296551952945411687998966643812980932107263560997083194173776982691610417838368495045583308992062915036063277420648945364840884160539937412227385219482687942930971346166301562186789538808790941858760716143964613421194365289600038396652875045618758526888338316747230797120922786007669897399569369906654101104782749622537434592461233103514691468855317382032207239712525881208853260755779175533347074376365100768240893007238012760274206726907045188911581696451140416768973735243777118595436533034235377191280865417884850119191629119002340008776621953599269430121861752762460562996174749493976914800027603345291120882319576355714163256459020267929219414048017461276721570966491935152828139213140973078155684722767112347818216391896198996707507566583359757862477261875390150374547347296496964304543209461383511078964384980607200574330538115332441235000890836463643030730600194654871198822983373649026962183633885778188764244629398073613610135886027031388163305224691672785242600519701868435922000339936196782687634440734378954588200401304643283333799348792851050170459611915016664469766604419431213191515934954313390527423662665812900771868365849397499558067400064838366643297671975245972648694677702073095039937100612473208477852323989944332637775011716430404082208069315986138180687793963096728014636341722222203940686023889898569821770411547361699275311879438468613596151473832879820932659876443011279342597867076513476035549710757544640334136092771232136526683925990001060860290453279728227214179390700118332097660164690660008165753555745901000454587054018904963904580459566558768365878720842425842311962676646565844000235763707765991815804454895291864591021303687059699554196047443027013293006951923689265818228078976365231752071961030850293972555727235937175612644125147071";

        public final static String name       = "testPassword";
        public final static String shortcut   = "1";
        public final static String comment    = "testComment";
        public final static String url        = "testUrl";
        public final static int    length     = 32;
        public final static int    shaCycles  = 185;

    };

    private RSA          rsa        = null;
    private String       fileName   = null;
    File                 vaultFile  = null;
    private byte[]       masterHash = null;

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
        try
        {
            masterHash = SHA.getHashBytes(masterPassword.getBytes());
            rsa = new RSA(SHA.getHashString(masterHash, SALT_P), SHA.getHashString(masterHash, SALT_Q),
                SHA.getHashString(masterHash, SALT_E));
            fileName = "testFile" + num.incrementAndGet();
            vaultFile = new File(Properties.PATHS.VAULT + fileName + Properties.EXTENSIONS.VAULT);
            if (vaultFile.exists()) vaultFile.delete();
        }
        catch (Exceptions e)
        {
            throw new Exception("pichaljka");
        }
    }

    @After
    public void tearDown() throws Exception
    {
        rsa = null;
        fileName = null;
        vaultFile.delete();
        vaultFile = null;
        try
        {
            Logger.getInstance().loggerOFF();
        }
        catch (Exceptions e)
        {
            System.out.println("Can't close logger");
        }
    }

    @Test
    public void testOldUserNoFile()
    {
        try
        {
            new Database(rsa, fileName, false, null);
            fail("Didn't caught any exception.");
        }
        catch (Exceptions e)
        {
            assertEquals(e.getCode(), XC.FILE_DOES_NOT_EXIST);
        }
        assertFalse(vaultFile.exists());
    }

    @Test
    public void testNewUser()
    {
        try
        {
            Database db = new Database(rsa, fileName, true, null);

            assertTrue(vaultFile.exists());
            assertEquals(db.getStatus(), Status.SYNCHRONIZED);
            assertTrue(db.getDecrypted().isEmpty());
            assertTrue(db.getName().isEmpty());
        }
        catch (Exceptions e)
        {
            fail("Exception error. " + e.getCode().toString());
        }
    }

    @Test
    public void testOldUserVaultName()
    {
        try
        {
            vaultFile.createNewFile();
        }
        catch (IOException e)
        {
            fail("Can't create vault file for test: " + e.toString());
        }

        try
        {
            Utilities.writeToFile(vaultFile.getAbsolutePath(), vaultNameTestData.cipherText);
        }
        catch (Exceptions e)
        {
            fail("Can't write to file: " + e.getCode().toString());
        }

        try
        {
            Database db = new Database(rsa, fileName, false, null);

            assertEquals(Status.SYNCHRONIZED, db.getStatus());
            assertEquals(0, db.getDecrypted().size());
            assertEquals(vaultNameTestData.vaultName, db.getName());
        }
        catch (Exceptions e)
        {
            fail("Exception error. " + e.getCode().toString());
        }
    }

    @Test
    public void testOldUserPasswords()
    {
        try
        {
            vaultFile.createNewFile();
        }
        catch (IOException e)
        {
            fail("Can't create vault file for test: " + e.toString());
        }
        try
        {
            Utilities.writeToFile(vaultFile.getAbsolutePath(), passwordTestData.cipherText);
        }
        catch (Exceptions e)
        {
            fail("Can't write to file: " + e.getCode().toString());
        }

        try
        {
            Database db = new Database(rsa, fileName, false, null);

            assertEquals(Status.SYNCHRONIZED, db.getStatus());
            assertEquals(1, db.getDecrypted().size());
            SpecialPassword pwd = db.getDecrypted().get(0);
            assertEquals(passwordTestData.name, pwd.getName());
            assertEquals(passwordTestData.comment, pwd.getComment());
            assertEquals(passwordTestData.shortcut, pwd.getShortcut());
            assertEquals(passwordTestData.url, pwd.getUrl());
            assertEquals(passwordTestData.length, pwd.getLength());
            assertEquals(passwordTestData.shaCycles, pwd.getShaCycles());
        }
        catch (Exceptions e)
        {
            fail("Exception error. " + e.getCode().toString());
        }

    }
}
