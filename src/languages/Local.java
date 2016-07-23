package languages;

import main.Exceptions;
import main.Properties;
import main.Settings;
import main.Terminator;

public class Local
{
    public enum Texts
    {
        // LABEL SINGLE-WORD

        LABEL_ABOUT(new String[]
        { "About " + Properties.SOFTWARE.NAME, "О " + Properties.SOFTWARE.NAME }),

        LABEL_AUTOLOGIN(new String[]
        { "Autologin", "Автоматическй вход" }),

        LABEL_CANCEL(new String[]
        { "Cancel", "Отменить" }),

        LABEL_COMMENT(new String[]
        { "Comment", "Комментарий" }),

        LABEL_CONTACTS(new String[]
        { "Contacts", "Контакты" }),

        LABEL_CREATE(new String[]
        { "Create", "Создать" }),

        LABEL_DELAY(new String[]
        { "Delay", "Задержка" }),

        LABEL_DELETE(new String[]
        { "Delete", "Удалить" }),

        LABEL_EDIT(new String[]
        { "Edit", "Редактировать" }),

        LABEL_EMPTY(new String[]
        { "", "" }),

        LABEL_ERROR(new String[]
        { "ERROR! ", "ОШИБКА! " }),

        LABEL_EXIT(new String[]
        { "Exit", "Выход" }),

        LABEL_FILE(new String[]
        { "File", "Файл" }),

        LABEL_GITHUB(new String[]
        { "Github", "Github" }),

        LABEL_HELP(new String[]
        { "Help", "Помощь" }),

        LABEL_LANGUAGE(new String[]
        { "Language", "Язык" }),

        LABEL_LENGTH(new String[]
        { "Length", "Длинна" }),

        LABEL_LINCENSE(new String[]
        { "License", "Лицензия" }),

        LABEL_LOGIN(new String[]
        { "Login", "Войти" }),

        LABEL_NAME(new String[]
        { "Name", "Имя" }),

        LABEL_NEW(new String[]
        { "New", "Новый" }),

        LABEL_OK(new String[]
        { "OK", "OK" }),

        LABEL_PASSWORD(new String[]
        { "Password", "Пароль" }),

        LABEL_PATH(new String[]
        { "Path", "Путь" }),

        LABEL_REGISTER(new String[]
        { "Register", "Регистрироваться" }),

        LABEL_RETYPE(new String[]
        { "Re-type", "Повторно" }),

        LABEL_SECONDS(new String[]
        { "Second(s)", "Секунд(ы)" }),

        LABEL_SETTINGS(new String[]
        { "Settings", "Настройки" }),

        LABEL_SHORTCUT(new String[]
        { "Shortcut", "Горячая клавиша" }),

        LABEL_SKIP(new String[]
        { "Skip", "Пропустить" }),

        LABEL_SPECIAL_CHARACTERS(new String[]
        { "Special characters", "Специальные символы" }),

        LABEL_UNNAMED(new String[]
        { "Unnamed", "Без имени" }),

        LABEL_UPDATE(new String[]
        { "Update", "Обновить" }),

        LABEL_URL(new String[]
        { "URL", "URL" }),

        LABEL_VAULT(new String[]
        { "Vault", "Хранилище" }),

        LABEL_VAULT_WITH_COLLS(new String[]
        { "Vault: ", "Хранилище: " }),

        LABEL_VERSION(new String[]
        { "v." + Properties.SOFTWARE.VERSION, "в." + Properties.SOFTWARE.VERSION }),

        // LABEL MULTI-WORD

        LABEL_ENTER_PASSWORD(new String[]
        { "Enter password", "Введите пароль" }),

        LABEL_INVALID_PASSWORD(new String[]
        { "Password is incorrect!", "Неправильный пароль!" }),

        LABEL_INVALID_PATH(new String[]
        { "Path is missing!", "Путь не указан!" }),

        LABEL_UNKNOWN_USER(new String[]
        { "Unknown user", "Неизвестный пользователь" }),

        // MESSAGES

        // MIGRATION

        MSG_MIGRATION_OCCURED(new String[]
        { "Legacy user file found.", "Обнаружен устаревший файл пользователя." }),

        MSG_MIGRATION_ERRORS(new String[]
        { " password(s) failed to migrate.", " пароль(ей) не удалось перенести." }),

        // ABOUT

        MSG_COPYRIGHT(new String[]
        { "Copyright (C) 2015  Andrejs Lubimovs, Vladislavs Varslavans", "Copyright (C) 2015  Andrejs Lubimovs, Vladislavs Varslavans" }),

        MSG_LICENSE_1(new String[]
        { "This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or(at your option) any later version.", "Это программа является свободным программным обеспечением. Вы можете распространять и/или модифицировать её согласно условиям Стандартной Общественной Лицензии GNU, опубликованной Фондом Свободного Программного Обеспечения, версии 3 или, по Вашему желанию, любой более поздней версии." }),

        MSG_LICENSE_2(new String[]
        { "This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.", "Эта программа распространяется в надежде, что она будет полезной, но БЕЗ ВСЯКИХ ГАРАНТИЙ, в том числе подразумеваемых гарантий ТОВАРНОГО СОСТОЯНИЯ ПРИ ПРОДАЖЕ и ГОДНОСТИ ДЛЯ ОПРЕДЕЛЁННОГО ПРИМЕНЕНИЯ. Смотрите Стандартную Общественную Лицензию GNU для получения дополнительной информации." }),

        MSG_LICENSE_3(new String[]
        { "You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.", "Вы должны были получить копию Стандартной Общественной Лицензии GNU вместе с программой. В случае её отсутствия, посмотрите <http://www.gnu.org/licenses/>." }),

        MSG_PROGRAM_DESCRIPTION(new String[]
        { "This program is a perfectly safe Password Manager and Generator, that will allow User, while remembering only one Password, maintain different Passwords for all the WEB and beyond.", "Эта программа является надёжным Генератором и Хранилищем паролей, которая позволяет пользователю помня только один Пароль иметь разные Пароли для разных ресурсов." }),

        // OTHER

        MSG_SHORTCUT_IN_USE_BY(new String[]
        { Texts.LABEL_ERROR.toString() + " Shortcut is in use by: ", Texts.LABEL_ERROR.toString() + " Горячая клавиша занята: " }),

        MSG_SHORTCUTS_MISSING(new String[]
        { "No shortcuts!", "Клавиши не определены!" }),

        // NOTE: not used currently
        MSG_SHORTCUTS_MISSING_ACTION(new String[]
        { "To use 'shortcuts menu', it's necessary to bind at least one shortcut in a vault.", "Для доступа к 'меню быстрого выбора', необходимо определить хотя-бы одну клавишу быстрого доступа в данном хранилище." }),

        MSG_VAULT_ALREADY_OPENED(new String[]
        { "Vault already opened!", "Хранилище уже открыто!" }),

        MSG_VAULTS_MISSING(new String[]
        { "There is no open vaults!", "Нет откртых хранилищ!" }),

        MSG_VAULTS_MISSING_ACTION(new String[]
        { "To use 'shortcuts menu', it's necessary to login to at least one vault.", "Для доступа к 'меню быстрого выбора', необходимо войти хотя-бы в одно хранилище." }),

        /**************************************************************************************************************
         * CREATE PASSWORD FORM
         *************************************************************************************************************/
        FORM_CREATEPWD_LABEL_HEADER(new String[]
        { "Enter new password data", "Введите данные для нового пароля" }),

        FORM_CREATEPWD_MSG_MISSING_PARAM(new String[]
        { Texts.LABEL_ERROR.toString() + "One or more mandatory fields are missing.", Texts.LABEL_ERROR.toString() + "Одно или более обязательных полей не заполнены." }),

        FORM_CREATEPWD_MSG_MUST_HAVE_SPECIAL(new String[]
        { "Must contain special characters", "Обязан иметь специальные символы" }),

        FORM_CREATEPWD_MSG_MUST_HAVE_UPPER(new String[]
        { "Must have UPPER case character", "Обязан иметь ПРОПИСНЫЕ буквы" }),

        FORM_CREATEPWD_MSG_NAME_EXISTS(new String[]
        { Texts.LABEL_ERROR.toString() + " Name already taken!", Texts.LABEL_ERROR.toString() + " Имя уже занято!" }),

        FORM_CREATEPWD_NAME(new String[]
        { "Create new password", "Создать новый пароль" }),

        /**************************************************************************************************************
         * DELETE PASSWORD FORM
         *************************************************************************************************************/
        FORM_DELETEPWD_MSG_NOTE(new String[]
        { "Enter this text to confirm deletion:", "Введите этот текст, чтобы подтвердить удаление:" }),

        FORM_DELETEPWD_NAME(new String[]
        { "Delete password", "Удалить пароль" }),

        FORM_DELETEPWD_MSG_CONFIRMATION(new String[]
        { "DELETE", "УДАЛИТЬ" }),

        /**************************************************************************************************************
         * EDIT PASSWORD FORM
         *************************************************************************************************************/
        FORM_EDITPWD_LABEL_HEADER(new String[]
        { "Edit password data", "Редактируйте данные пароля" }),

        FORM_EDITPWD_NAME(new String[]
        { "Edit Password", "Редактировать пароль" }),

        /**************************************************************************************************************
         * LOGIN FORM
         *************************************************************************************************************/

        FORM_LOGIN_MSG_INCORRECT_PWD(new String[]
        { "Password incorrect!", "Неверный пароль!" }),

        FORM_LOGIN_MSG_PWDS_DONT_MATCH(new String[]
        { "Passwords don't match!", "Пароли не совпадают!" }),

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

        /**************************************************************************************************************
         * RESET PASSWORD FORM
         *************************************************************************************************************/
        FORM_RESETPWD_MSG_WARNING(new String[]
        { "By pressing \'OK\' you will substitute current Password with the new Password.Thus current Password will be permanently lost!", "Нажав \'OK\' Вы замените текущий Пароль на Новый.В результатье текущий Пароль будет безвозвратно утерян!" }),

        FORM_RESETPWD_NAME(new String[]
        { "Reset password", "Обновить пароль" }),

        FORM_RESETWD_LABEL_CURRENT(new String[]
        { "Current", "Текущий" }),

        /**************************************************************************************************************
         * SETTINGS PASSWORD FORM
         *************************************************************************************************************/

        /**************************************************************************************************************
         * UPDATE FORM
         *************************************************************************************************************/
        FORM_UPDATE_LABEL_CHECK(new String[]
        { "Searching for available updates...", "Поиск доступных обновлений..." }),

        FORM_UPDATE_LABEL_DOWNLOAD(new String[]
        { "Downloading updates...", "Загрузка обновлений..." }),

        FORM_UPDATE_LABEL_INSTALL(new String[]
        { "Installing...", "Установка..." }),

        FORM_UPDATE_MSG_UPDATE_AVAILABLE(new String[]
        { "New version available.", "Доступна новая версия." }),

        /**************************************************************************************************************
         * MENU FORM
         *************************************************************************************************************/

        /**************************************************************************************************************
         * TRAY FORM
         *************************************************************************************************************/
        TRAY_MSG_FAILED_LOAD_SETTINGS(new String[]
        { "FAILED to load settings. Using DEFAULTS!", "Не удалось загрузить настройки. Используются настройки по-умлочанию!" }),

        TRAY_MSG_PWD_COPIED_TO_CLIPBOARD(new String[]
        { "Password copied to Clipboard", "Пароль помещён в буфер обмена" }),

        TRAY_MSG_PWD_REMOVED_FROM_CLIPBOARD(new String[]
        { "Password removed from Clipboard", "Пароль убран из буфера обмена" }),

        TRAY_MSG_TIME_LEFT(new String[]
        { "Time left", "Времени осталось" }),

        TRAY_MSG_FAILED_TO_AUTOLOGIN(new String[]
        { "Autologin failed.", "Автоматический вход не удался." }),

        TRAY_MSG_FAILED_TO_UPDATE(new String[]
        { "Update failed.", "Обновление не удалось." }),

        /**************************************************************************************************************
         * EXPORT FORM
         *************************************************************************************************************/

        FORM_EXPORT_LABEL_EXPORT(new String[]
        { "Export", "Экспорт" }),

        FORM_EXPORT_MSG_SELECT_PATH(new String[]
        { "Select path to save export file.", "Выберите путь для сохранения файла экспорта." }),

        /**************************************************************************************************************
         * TEMPLATE
         *************************************************************************************************************/
        Z_ETHALON(new String[]
        { "", "" }),

        ;

        private final String text[];

        private Texts(final String[] text)
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
}
