/**
 *
 */
package languages;

import main.Exceptions;
import main.Settings;
import main.Terminator;

/**
 * @author curious-odd-man
 *
 */
public class Texts
{
    //@fmt:off
    /**************************************************************************************************************
     * SYNTAX
     *
     * <SCOPE>_[<SCOPE_NAME>]_<ELEMENT>_[<ELEMENT_NAME>]
     *
     * ELEMENT : {
     *              LABEL - captions
     *              NAME - window header
     *              MSG  - text
     *           }
     *************************************************************************************************************/
    //@fmt:on
    public enum TextID
    {
        /**************************************************************************************************************
         * COMMON TEXTS
         *************************************************************************************************************/
        COMMON_APPLICATION_NAME(new String[]
        { "pasSHA", "pasSHA" }),

        COMMON_LABEL_CANCEL(new String[]
        { "Cancel", "Отменить" }),

        COMMON_LABEL_ERROR(new String[]
        { "ERROR!", "ОШИБКА!" }),

        COMMON_LABEL_NEW(new String[]
        { "New", "Новый" }),

        COMMON_LABEL_OK(new String[]
        { "OK", "OK" }),

        COMMON_LABEL_SECONDS(new String[]
        { "second(s)", "секунд(ы)" }),

        COMMON_LABEL_VERSION(new String[]
        { "v." + version, "в." + version }),

        /**************************************************************************************************************
         * CREATE PASSWORD FORM
         *************************************************************************************************************/
        FORM_CREATEPWD_LABEL_COMMENT(new String[]
        { "Comment", "Комментарий" }),

        FORM_CREATEPWD_LABEL_CREATE(new String[]
        { "Create", "Создать" }),

        FORM_CREATEPWD_LABEL_LENGTH(new String[]
        { "Length", "Длинна" }),

        FORM_CREATEPWD_LABEL_NAME(new String[]
        { "Name", "Имя" }),

        FORM_CREATEPWD_LABEL_SPECIAL_CHARACTERS(new String[]
        { "Special characters", "Специальные символы" }),

        FORM_CREATEPWD_LABEL_URL(new String[]
        { "URL", "URL" }),

        FORM_CREATEPWD_MANAGER_LABEL_DELETE(new String[]
        { "Delete", "Удалить" }),

        FORM_CREATEPWD_MSG_MISSING_PARAM(new String[]
        {
                TextID.COMMON_LABEL_ERROR.toString() + "One or more mandatory fields are missing.",
                TextID.COMMON_LABEL_ERROR.toString()
                        + "Одно или более обязательных полей не заполнены." }),

        FORM_CREATEPWD_MSG_MUST_HAVE_SPECIAL(new String[]
        { "Must contain special characters", "Обязан иметь специальные символы" }),

        FORM_CREATEPWD_MSG_MUST_HAVE_UPPER(new String[]
        { "Must have UPPER case character", "Обязан иметь ПРОПИСНЫЕ буквы" }),

        FORM_CREATEPWD_MSG_NAME_EXISTS(new String[]
        { TextID.COMMON_LABEL_ERROR.toString() + " Name already taken!",
                TextID.COMMON_LABEL_ERROR.toString() + " Имя уже занято!" }),

        FORM_CREATEPWD_NAME(new String[]
        { "Create new password", "Создать новый пароль" }),

        /**************************************************************************************************************
         * DELETE PASSWORD FORM
         *************************************************************************************************************/
        FORM_DELETEPWD_MSG_NOTE(
                new String[]
                { "Enter this text to confirm deletion:",
                        "Введите этот текст, чтобы подтвердить удаление:" }),

        FORM_DELETEPWD_NAME(new String[]
        { "Delete password", "Удалить пароль" }),

        /**************************************************************************************************************
         * EDIT PASSWORD FORM
         *************************************************************************************************************/
        FORM_EDITPWD_LABEL_SHORTCUT(new String[]
        { "Shortcut", "Горячая клавиша" }),

        FORM_EDITPWD_NAME(new String[]
        { "Edit Password", "Редактировать пароль" }),

        FORM_EDITPWD_MSG_SHORTCUT_IN_USE(new String[]
        { TextID.COMMON_LABEL_ERROR.toString() + " Shortcut is in use by: ",
                TextID.COMMON_LABEL_ERROR.toString() + " Горячая клавиша занята: " }),

        /**************************************************************************************************************
         * LOGIN FORM
         *************************************************************************************************************/
        FORM_LOGIN_LABEL_ENTER_PWD(new String[]
        { "Enter Password", "Введите пароль" }),

        FORM_LOGIN_LABEL_LOGIN(new String[]
        { "Login", "Войти" }),

        FORM_LOGIN_LABEL_PASSWORD(new String[]
        { "Password", "Пароль" }),

        FORM_LOGIN_LABEL_REGISTER(new String[]
        { "Register", "Регистрироваться" }),

        FORM_LOGIN_LABEL_RETYPE(new String[]
        { "Re-type", "Повторно" }),

        FORM_LOGIN_MSG_INCORRECT_PWD(new String[]
        { "Password incorrect!", "Неверный пароль!" }),

        FORM_LOGIN_MSG_PWDS_DONT_MATCH(new String[]
        { "Passwords don't match!", "Пароли не совпадают!" }),

        FORM_LOGIN_NAME(new String[]
        { "Login", "Вход" }),

        /**************************************************************************************************************
         * MANAGE PASSWORDS FORM
         *************************************************************************************************************/
        FORM_MANAGEPWD_LABEL_COPY_TO_CLIPBOARD(new String[]
        { "Copy To Clipboard", "Поместить в буфер обмена" }),

        FORM_MANAGEPWD_LABEL_EXPORT(new String[]
        { "Export", "Экспортировать" }),

        FORM_MANAGEPWD_LABEL_PWD_NAME(new String[]
        { "Password name", "Название пароля" }),

        FORM_MANAGEPWD_LABEL_RESET(new String[]
        { "Reset password", "Обновить пароль" }),

        FORM_MANAGEPWD_NAME(new String[]
        { "Password Manager", "Управление паролями" }),

        FORM_MANAGEWD_LABEL_EDIT(new String[]
        { "Edit", "Редактировать" }),

        /**************************************************************************************************************
         * RESET PASSWORD FORM
         *************************************************************************************************************/
        FORM_RESETPWD_MSG_WARNING(
                new String[]
                {
                        "By pressing \'OK\' you will substitute current Password with the new Password.\n Thus current Password will be permanently lost!",
                        "Нажав \'OK\' Вы замените текущий Пароль на Новый.\n В результатье текущий Пароль будет безвозвратно утерян!" }),

        FORM_RESETPWD_NAME(new String[]
        { "Reset password", "Обновить пароль" }),

        FORM_RESETWD_LABEL_CURRENT(new String[]
        { "Current", "Текущий" }),

        /**************************************************************************************************************
         * SETTINGS PASSWORD FORM
         *************************************************************************************************************/
        FORM_SETTINGS_LABEL_DELAY(new String[]
        { "Delay", "Задержка" }),

        FORM_SETTINGS_LABEL_LANGUAGE(new String[]
        { "Language", "Язык" }),

        FORM_SETTINGS_NAME(new String[]
        { "Settings", "Настройки" }),

        /**************************************************************************************************************
         * UPDATE FORM
         *************************************************************************************************************/
        FORM_UPDATE_LABEL_CHECK(new String[]
        { "Searching for available updates...", "Поиск доступных обновлений..." }),

        FORM_UPDATE_LABEL_DOWNLOAD(new String[]
        { "Downloading updates...", "Загрузка обновлений..." }),

        FORM_UPDATE_LABEL_INSTALL(new String[]
        { "Installing...", "Установка..." }),

        FORM_UPDATE_LABEL_RESTART(new String[]
        { "YOUR TEXT HERE", "YOUR TEXT HERE" }), // TODO

        FORM_UPDATE_LABEL_UPDATE(new String[]
        { "Update", "Обновить" }),

        FORM_UPDATE_LABEL_SKIP(new String[]
        { "Skip", "Пропустить" }),

        FORM_UPDATE_MSG_UPDATE_AVAILABLE(new String[]
        { "New version available.", "Доступна новая версия." }),

        /**************************************************************************************************************
         * MENU FORM
         *************************************************************************************************************/
        MENU_LABEL_EXIT(new String[]
        { "Exit", "Выход" }),

        MENU_LABEL_FILE(new String[]
        { "File", "Файл" }),

        /**************************************************************************************************************
         * TRAY FORM
         *************************************************************************************************************/
        TRAY_MSG_FAILED_LOAD_SETTINGS(new String[]
        { "FAILED to load settings. Using DEFAULTS!",
                "Не удалось загрузить настройки. Используются настройки по-умлочанию!" }),

        TRAY_MSG_PWD_COPIED_TO_CLIPBOARD(new String[]
        { "Password copied to Clipboard", "Пароль помещён в буфер обмена" }),

        TRAY_MSG_PWD_REMOVED_FROM_CLIPBOARD(new String[]
        { "Password removed from Clipboard", "Пароль убран из буфера обмена" }),

        TRAY_MSG_TIME_LEFT(new String[]
        { "Time left", "Времени осталось" }),

        TRAY_MSG_FAILED_TO_UPDATE(new String[]
        { "Update failed.", "Обновление не удалось." }),

        /**************************************************************************************************************
         * TEMPLATE
         *************************************************************************************************************/
        Z_ETHALON(new String[]
        { "", "" }),

        ;

        private final String text[];

        private TextID(final String[] text)
        {
            this.text = text;
        }

        @Override
        public String toString()
        {
            try
            {
                return text[Settings.getInstance().getLanguage()];
            }
            catch (Exceptions e)
            {
                Terminator.terminate(e);
            }

            return "CRASH";
        }
    }

    static private String version = "1.2";

    public static String getVersion()
    {
        return version;
    }

}
