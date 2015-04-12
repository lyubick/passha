/**
 *
 */
package languages;

import main.Settings;

/**
 * @author curious-odd-man
 *
 */
public class Texts
{
    // TODO: Use String.split to parse CSV file with texts
    public enum TextID
    {
        ADD_SPECIAL_PASSWORD(new String[]
        { "Add Special Password", "Добавить новый пароль" }),

        CALCULATING(new String[]
        { "Calculating...", "Вычисдяем..." }),

        CANCEL(new String[]
        { "Cancel", "Отменить" }),

        CHANGE_PWD_HEADER(new String[]
        { "Password reset", "Обновление пароля" }),

        CHANGE_PWD_WARNING(
                new String[]
                {
                        "By pressing OK you will substitute Current Password with New one.\n Thus Current Password will be permanently lost!",
                        "Нажав \"Подтвердить\" Вы замените Настоящий Пароль Новым.\n В результатье Настоящий Пароль будет безвозвратно утерян!" }),

        COMMENT(new String[]
        { "Comment", "Комментарий" }),

        COPY_CLIPBOARD(new String[]
        { "Copy To Clipboard", "Скопировать в буфер обмена" }),

        CREATE(new String[]
        { "Create", "Создать" }),

        CURRENT_PWD(new String[]
        { "Current", "Настоящий" }),

        DELAY(new String[]
        { "Delay", "Задержка" }),

        DELETE(new String[]
        { "Delete", "Удалить" }),

        DISCARD(new String[]
        { "Discard", "Отменить" }),

        ENTER_PASSWORD(new String[]
        { "Enter Password", "Введите пароль" }),

        ERR_MISSING_PASSWORD_NAME(new String[]
        { "Error! Password name must be entered!", "Ошибка! Необходимо ввести имя Пароля!" }),

        ERR_NAME_ALREADY_TAKEN(new String[]
        { "Error! Name already taken!", "Ошибка! Имя уже занято!" }),

        EXIT(new String[]
        { "Exit", "Выход" }),

        EXPORT(new String[]
        { "Export", "Экспортировать" }),

        FILE(new String[]
        { "File", "Файл" }),

        GREETING(new String[]
        { "Welcome to pasSHA!", "Добро пожаловать в pasSHA!" }),

        LANGUAGE(new String[]
        { "Language", "Язык" }),

        LENGTH(new String[]
        { "Length", "Длинна" }),

        LOADING(new String[]
        { "Loading", "Загрузка" }),

        LOGIN(new String[]
        { "Login", "Регистрация" }),

        MANAGE(new String[]
        { "Manage", "Управление" }),

        MS(new String[]
        { "milliseconds", "миллисекунды" }),

        MUST_CONTAIN_UPPER_CASE_CHAR(new String[]
        { "Must have UPPER case character", "Обязан иметь ПРОПИСНЫЕ буквы" }),

        MUST_CONTAINT_SPECIAL_CHARS(new String[]
        { "Must contain special characters", "Обящан иметь специальные символы" }),

        NAME(new String[]
        { "Name", "Имя" }),

        NEW(new String[]
        { "New", "Добавить" }),

        OK(new String[]
        { "OK", "Подтвердить" }),

        PASSWORD_COPIED(new String[]
        { "Password copied to Clipboard", "Пароль помещён в буфер обмена" }),

        PASSWORD_INCORRECT(new String[]
        { "Password incorrect!", "Не верный пароль!" }),

        PASSWORD_REMOVED(new String[]
        { "Password removed from Clipboard", "Пароль убран из буфера обмена" }),

        PASSWORDS_DONT_MATCH(new String[]
        { "Passwords don't match!", "Пароли не идентичны" }),

        PROGRAM_NAME(new String[]
        { "pasSHA", "pasSHA" }),

        PWD(new String[]
        { "Password", "Пароль" }),

        PWD_NAME(new String[]
        { "Password name", "Имя пароля" }),

        REGISTER(new String[]
        { "Register", "Регистрироваться" }),

        RESET_PASSWORD(new String[]
        { "Reset password", "Обновить пароль" }),

        RETYPE(new String[]
        { "Re-type", "Повторите" }),

        S(new String[]
        { "second(s)", "секунд(ы)" }),

        SAVE(new String[]
        { "Save", "Сохранить" }),

        SAVE_CONFIRMATION(new String[]
        { "There are unsaved changes. Save before exit?",
                "Есть несохранённые изменения. Сохранить?" }),

        SAVING(new String[]
        { "Saving", "Сохранение" }),

        SETTINGS(new String[]
        { "Settings", "Настройки" }),

        SHORTCUT(new String[]
        { "Shortcut", "Горячая клавиша" }),

        SPECIAL_CHARACTERS(new String[]
        { "Special characters", "Специальные символы" }),

        TIME_LEFT(new String[]
        { "Time left", "Времени осталось" }),

        URL(new String[]
        { "URL", "URL" }),

        VERSION(new String[]
        { "v." + version, "в." + version }),

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
            return text[Settings.getLanguage()];
        }
    }

    static String version = "2.0.000";
}
