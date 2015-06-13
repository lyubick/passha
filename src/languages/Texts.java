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
    // TODO: Use String.split to parse CSV file with texts
    public enum TextID
    {
        COMMON_LABEL_APP_NAME(new String[]
        { "pasSHA", "pasSHA" }),

        COMMON_LABEL_CANCEL(new String[]
        { "Cancel", "Отменить" }),

        COMMON_LABEL_ERROR(new String[]
        { "ERROR!", "ОШИБКА!" }),

        COMMON_LABEL_OK(new String[]
        { "OK", "OK" }),

        COMMON_LABEL_SECONDS(new String[]
        { "second(s)", "секунд(ы)" }),

        COMMON_LABEL_VERSION(new String[]
        { "v." + version, "в." + version }),

        FORM_LOGIN_ERROR_PWD_INCORRECT(new String[]
        { "Password incorrect!", "Неверный пароль!" }),

        FORM_LOGIN_ERROR_PWDS_DONT_MATCH(new String[]
        { "Passwords don't match!", "Пароли не совпадают!" }),

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

        FORM_MENU_LABEL_EXIT(new String[]
        { "Exit", "Выход" }),

        FORM_MENU_LABEL_FILE(new String[]
        { "File", "Файл" }),

        FORM_PWD_CHANGE_LABEL_CURRENT(new String[]
        { "Current", "Нынешний" }),

        FORM_PWD_CHANGE_LABEL_NEW(new String[]
        { "New", "Новый" }),

        FORM_PWD_CHANGE_NAME(new String[]
        { "Update Password", "Обновить пароль" }),

        FORM_PWD_CHANGE_WARNING(
                new String[]
                {
                        "By pressing \'OK\' you will substitute current Password with the new Password.\n Thus current Password will be permanently lost!",
                        "Нажав \'OK\' Вы замените существующий Пароль на Новый.\n В результатье существующий Пароль будет безвозвратно утерян!" }),

        FORM_PWD_MANAGER_LABEL_COPY_TO_CLIPBOARD(new String[]
        { "Copy To Clipboard", "Скопировать в буфер обмена" }),

        FORM_PWD_MANAGER_LABEL_EXPORT(new String[]
        { "Export", "Экспортировать" }),

        FORM_PWD_MANAGER_LABEL_RESET(new String[]
        { "Reset password", "Обновить пароль" }),

        FORM_PWD_MANAGER_TABLE_LABEL_PWD_NAME(new String[]
        { "Password name", "Название пароля" }),

        FORM_SETTINGS_LABEL_DELAY(new String[]
        { "Delay", "Задержка" }),

        FORM_SETTINGS_LABEL_LANGUAGE(new String[]
        { "Language", "Язык" }),

        FORM_SETTINGS_NAME(new String[]
        { "Settings", "Настройки" }),

        FORM_SP_LABEL_COMMENT(new String[]
        { "Comment", "Комментарий" }),

        FORM_SP_LABEL_CREATE(new String[]
        { "Create", "Создать" }),

        FORM_SP_LABEL_ERROR_MISSING_PARAM(
                new String[]
                { "Error! One or more mandatory fields are missing.",
                        "Ошибка! Одно или более обязательных полей не заполнены." }),

        FORM_SP_LABEL_ERROR_NAME_EXISTS(new String[]
        { "Error! Name already taken!", "Ошибка! Имя уже занято!" }),

        FORM_SP_LABEL_LENGTH(new String[]
        { "Length", "Длинна" }),

        FORM_SP_LABEL_MUST_HAVE_SPECIAL(new String[]
        { "Must contain special characters", "Обящан иметь специальные символы" }),

        FORM_SP_LABEL_MUST_HAVE_UPPER(new String[]
        { "Must have UPPER case character", "Обязан иметь ПРОПИСНЫЕ буквы" }),

        FORM_SP_LABEL_NAME(new String[]
        { "Name", "Имя" }),

        FORM_SP_LABEL_SPECIAL_CHARACTERS(new String[]
        { "Special characters", "Специальные символы" }),

        FORM_SP_LABEL_URL(new String[]
        { "URL", "URL" }),

        FORM_SP_MANAGER_LABEL_DELETE(new String[]
        { "Delete", "Удалить" }),

        FORM_SP_NAME(new String[]
        { "Create new password", "Создать новый пароль" }),

        TRAY_MESSAGE_ERROR_FAILED_LOAD_SETTINGS(new String[]
        { "FAILED to load settings. Using DEFAULTS!",
                "Не удалось загрузить настройки. Используются настройки по-умлочанию!" }),

        TRAY_MESSAGE_INFO_COPIED_TO_CLIPBOARD(new String[]
        { "Password copied to Clipboard", "Пароль помещён в буфер обмена" }),

        TRAY_MESSAGE_INFO_PWD_REMOVED_FROM_CLIPBOARD(new String[]
        { "Password removed from Clipboard", "Пароль убран из буфера обмена" }),

        TRAY_MESSAGE_TIME_LEFT(new String[]
        { "Time left", "Времени осталось" }),

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

    static String version = "2.0.000";
}
