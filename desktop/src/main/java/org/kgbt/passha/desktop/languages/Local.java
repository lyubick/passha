package org.kgbt.passha.desktop.languages;

import org.kgbt.passha.core.common.Exceptions;
import org.kgbt.passha.core.common.cfg.Properties;
import org.kgbt.passha.core.common.cfg.Settings;
import org.kgbt.passha.core.common.Terminator;

public class Local {
    public enum Texts {
        // LABEL SINGLE-WORD

        LABEL_ABOUT(new String[]
                {"About " + Properties.SOFTWARE.NAME, "О " + Properties.SOFTWARE.NAME}),

        LABEL_AUTOLOGIN(new String[]
                {"Autologin", "Автоматический вход"}),

        LABEL_CANCEL(new String[]
                {"Cancel", "Отмена"}),

        LABEL_COMMENT(new String[]
                {"Comment", "Комментарий"}),

        LABEL_CONTACTS(new String[]
                {"Contacts", "Контакты"}),

        LABEL_CREATE(new String[]
                {"Create", "Создать"}),

        LABEL_DELAY(new String[]
                {"Delay", "Задержка"}),

        LABEL_DELETE(new String[]
                {"Delete", "Удалить"}),

        LABEL_EDIT(new String[]
                {"Edit", "Редактировать"}),

        LABEL_EMPTY(new String[]
                {"", ""}),

        LABEL_ERROR(new String[]
                {"ERROR! ", "ОШИБКА! "}),

        LABEL_EXIT(new String[]
                {"Exit", "Выход"}),

        LABEL_FILE(new String[]
                {"File", "Файл"}),

        LABEL_GITHUB(new String[]
                {"Github", "Github"}),

        LABEL_HELP(new String[]
                {"Help", "Помощь"}),

        LABEL_LANGUAGE(new String[]
                {"Language", "Язык"}),

        LABEL_LENGTH(new String[]
                {"Length", "Длинна"}),

        LABEL_LINCENSE(new String[]
                {"License", "Лицензия"}),

        LABEL_LOGIN(new String[]
                {"Login", "Вход"}),

        LABEL_NAME(new String[]
                {"Name", "Имя"}),

        LABEL_NEW(new String[]
                {"New", "Новый"}),

        LABEL_OK(new String[]
                {"OK", "OK"}),

        LABEL_PASSWORD(new String[]
                {"Password", "Пароль"}),

        LABEL_PATH(new String[]
                {"Path", "Путь"}),

        LABEL_REGISTER(new String[]
                {"Register", "Регистрироваться"}),

        LABEL_RETYPE(new String[]
                {"Re-type", "Повторить"}),

        LABEL_SECONDS(new String[]
                {"Second(s)", "Секунд(ы)"}),

        LABEL_SETTINGS(new String[]
                {"Settings", "Настройки"}),

        LABEL_SHORTCUT(new String[]
                {"Shortcut", "Бытсрая клавиша"}),

        LABEL_SKIP(new String[]
                {"Skip", "Пропустить"}),

        LABEL_SPECIAL_CHARACTERS(new String[]
                {"Special characters", "Специальные символы"}),

        LABEL_UNNAMED(new String[]
                {"Unnamed", "Без имени"}),

        LABEL_UPDATE(new String[]
                {"Update", "Обновить"}),

        LABEL_URL(new String[]
                {"URL", "URL"}),

        LABEL_VAULT(new String[]
                {"Vault", "Хранилище"}),

        LABEL_VAULT_WITH_COLLS(new String[]
                {"Vault: ", "Хранилище: "}),

        LABEL_VERSION(new String[]
                {"v." + Properties.SOFTWARE.VERSION, "в." + Properties.SOFTWARE.VERSION}),

        // LABEL MULTI-WORD

        LABEL_ENTER_PASSWORD(new String[]
                {"Enter password", "Введите пароль"}),

        LABEL_INVALID_PASSWORD(new String[]
                {"Password is incorrect!", "Пароль не правильный!"}),

        LABEL_INVALID_PATH(new String[]
                {"Path is missing!", "Путь не существует!"}),

        LABEL_MIGRATION_TITLE(new String[]
                {"User file migration", "Миграция файла пользователя"}),

        LABEL_UNKNOWN_USER(new String[]
                {"Unknown user", "Неизвестный пользователь"}),

        // MESSAGES

        // MIGRATION

        MSG_MIGRATION_OCCURED(new String[]
                {"Legacy user file found.", "Найден устаревший файл пользователя."}),

        MSG_MIGRATION_ERRORS(new String[]
                {" password(s) failed to migrate.", " пароль(я/ей) не удалось смигрировать."}),

        MSG_MIGRATION_SUCCESS(new String[]
                {"Successfully migrated user file.", "Файл пользователя удачно смигрирован."}),

        // ABOUT

        MSG_COPYRIGHT(new String[]
                {"Copyright (C) 2015  Andrejs Ļubimovs, Vladislavs Varslavāns", "Авторское право (C) 2015  Andrejs Ļubimovs, Vladislavs Varslavāns"}),

        MSG_LICENSE_1(new String[]
                {"This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or(at your option) any later version.",
                        "This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or(at your option) any later version."}),

        MSG_LICENSE_2(new String[]
                {"This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.",
                        "This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details."}),

        MSG_LICENSE_3(new String[]
                {"You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.",
                        "You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>."}),

        MSG_PROGRAM_DESCRIPTION(new String[]
                {"This program is a perfectly safe Password Manager and Generator, that will allow User, while remembering only one Password, maintain different Passwords for all the WEB and beyond.",
                        "..."}),

        // OTHER

        MSG_CONFIRM_UNSAFE_EXIT(new String[]
        {"There is unsaved passwords, exit?",
                "Есть не сохранённые пароли, выйти?"}),

        MSG_DB_SYNC_FAILED(new String[]
                {"Database synchronization failed.",
                        "Не удалось синхронизировать базу данных."}),

        MSG_DB_SYNC_SUCCESS(new String[]
                {"Database is synchronized.",
                        "База данных синхронизирована."}),

        MSG_DB_SYNC_UNAVAILABLE(new String[]
                {"N/A", "Н/Д"}),

        MSG_SHORTCUT_IN_USE_BY(new String[]
                {Texts.LABEL_ERROR.toString() + " Shortcut is in use by: ",
                        Texts.LABEL_ERROR.toString() + " Сокращённая комбинация уже используется: "}),

        MSG_SHORTCUTS_MISSING(new String[]
                {"No shortcuts!", "Нет сокращённых комбинаций клавиш!"}),

        // NOTE: not used currently
        MSG_SHORTCUTS_MISSING_ACTION(new String[]
                {"To use 'shortcuts menu', it's necessary to bind at least one shortcut in a vault.",
                        "Для использования 'меню быстрого доступа' необходимо задать хотя бы одну горячую клавишу в хранилище."}),

        MSG_VAULT_ALREADY_OPENED(new String[]
                {"Vault already opened!", "Хранилище уже открыто!"}),

        MSG_VAULTS_MISSING(new String[]
                {"There is no open vaults!", "Нет открытых хранилищ!"}),

        MSG_VAULTS_MISSING_ACTION(new String[]
                {"To use 'shortcuts menu' it's necessary to login to at least one vault.",
                        "Для использования 'меню быстрого доступа' необходимо открыть хотя бы одно хранилище."}),

        /**************************************************************************************************************
         * CREATE PASSWORD FORM
         *************************************************************************************************************/
        FORM_CREATEPWD_LABEL_HEADER(new String[]
                {"Enter new password data", "Введите новые данные пароля"}),

        FORM_CREATEPWD_MSG_MISSING_PARAM(new String[]
                {Texts.LABEL_ERROR.toString() + "One or more mandatory fields are missing.",
                        Texts.LABEL_ERROR.toString() + "Одно или более полей не зополнены"}),

        FORM_CREATEPWD_MSG_MUST_HAVE_SPECIAL(new String[]
                {"Must contain $peci@l characters", "Обязан содержать $пеци@льные символы"}),

        FORM_CREATEPWD_MSG_MUST_HAVE_UPPER(new String[]
                {"Must have UPPER case character", "Обязан содержать ЗАГЛАВНЫЕ символы"}),

        FORM_CREATEPWD_MSG_NAME_EXISTS(new String[]
                {Texts.LABEL_ERROR.toString() + " Name already taken!", Texts.LABEL_ERROR.toString() + " имя уже используется!"}),

        FORM_CREATEPWD_NAME(new String[]
                {"Create new password", "Создать новый пароль"}),

        /**************************************************************************************************************
         * DELETE PASSWORD FORM
         *************************************************************************************************************/
        FORM_DELETEPWD_MSG_NOTE(new String[]
                {"Enter this text to confirm deletion:",
                        "Введите этот текст как подтверждение удаления:"}),

        FORM_DELETEPWD_NAME(new String[]
                {"Delete password", "Удалить пароль"}),

        FORM_DELETEPWD_MSG_CONFIRMATION(new String[]
                {"DELETE", "УДАЛИТЬ"}),

        /**************************************************************************************************************
         * EDIT PASSWORD FORM
         *************************************************************************************************************/
        FORM_EDITPWD_LABEL_HEADER(new String[]
                {"Edit password data", "Редактировать данные пароля"}),

        FORM_EDITPWD_NAME(new String[]
                {"Edit Password", "Редактировать пароль"}),

        /**************************************************************************************************************
         * LOGIN FORM
         *************************************************************************************************************/

        FORM_LOGIN_MSG_INCORRECT_PWD(new String[]
                {"Password incorrect!", "Неправильный пароль!"}),

        FORM_LOGIN_MSG_PWDS_DONT_MATCH(new String[]
                {"Passwords don't match!", "Пароли не совпадают!"}),

        /**************************************************************************************************************
         * MANAGE PASSWORDS FORM
         *************************************************************************************************************/
        FORM_MANAGEPWD_LABEL_COPY_TO_CLIPBOARD(new String[]
                {"Copy to clipboard", "Поместить в буфер обмена"}),

        FORM_MANAGEPWD_LABEL_AUTO_TYPE(new String[]
                {"Auto-Type", "Авто-ввод"}),

        FORM_MANAGEPWD_LABEL_EXPORT(new String[]
                {"Export", "Экспорт"}),

        FORM_MANAGEPWD_LABEL_PWD_NAME(new String[]
                {"Password name", "Название пароля"}),

        FORM_MANAGEPWD_LABEL_RESET(new String[]
                {"Reset password", "Обновить пароль"}),

        FORM_MANAGEPWD_NAME(new String[]
                {"Password Manager", "Менеджер Паролей"}),

        /**************************************************************************************************************
         * RESET PASSWORD FORM
         *************************************************************************************************************/
        FORM_RESETPWD_MSG_WARNING(new String[]
                {"By pressing \'OK\' you will substitute current Password with the new Password. Thus current Password will be permanently lost!",
                        "Нажав \'OK\' Вы замените текущий Пароль на новый. Текущий пароль будет безвозвратно утерен!"}),

        FORM_RESETPWD_NAME(new String[]
                {"Reset password", "Обновить пароль"}),

        FORM_RESETWD_LABEL_CURRENT(new String[]
                {"Current", "Текущий"}),

        /**************************************************************************************************************
         * SETTINGS PASSWORD FORM
         *************************************************************************************************************/

        /**************************************************************************************************************
         * UPDATE FORM
         *************************************************************************************************************/
        FORM_UPDATE_LABEL_CHECK(new String[]
                {"Searching for available updates...", "Поиск доступных обновлений..."}),

        FORM_UPDATE_LABEL_DOWNLOAD(new String[]
                {"Downloading updates...", "Обновление скачивается..."}),

        FORM_UPDATE_LABEL_INSTALL(new String[]
                {"Installing...", "Установка..."}),

        FORM_UPDATE_MSG_UPDATE_AVAILABLE(new String[]
                {"New version available.", "Доступна новая версия."}),

        /**************************************************************************************************************
         * MENU FORM
         *************************************************************************************************************/

        /**************************************************************************************************************
         * TRAY FORM
         *************************************************************************************************************/
        TRAY_MSG_FAILED_LOAD_SETTINGS(new String[]
                {"FAILED to load settings. Using DEFAULTS!",
                        "Не удалось загрузить настройки. Будут использованы настройки по умолчанию!"}),

        TRAY_MSG_PWD_COPIED_TO_CLIPBOARD(new String[]
                {"Password copied to Clipboard", "Пароль помещён в буфер обмена"}),

        TRAY_MSG_PWD_REMOVED_FROM_CLIPBOARD(new String[]
                {"Password removed from Clipboard", "Пароль убран из Буфера Обмена"}),

        TRAY_MSG_TIME_LEFT(new String[]
                {"Time left", "Времени осталось"}),

        TRAY_MSG_FAILED_TO_AUTOLOGIN(new String[]
                {"Autologin failed.", "Автоматическмй вход не удался."}),

        TRAY_MSG_FAILED_TO_UPDATE(new String[]
                {"Update failed.", "Обновление не удалось."}),

        /**************************************************************************************************************
         * EXPORT FORM
         *************************************************************************************************************/

        FORM_EXPORT_LABEL_EXPORT(new String[]
                {"Export", "Экспорт"}),

        FORM_EXPORT_MSG_SELECT_PATH(new String[]
                {"Select path to save export file.", "Выберите путь для экспортирования паролей."}),

        /**************************************************************************************************************
         * TEMPLATE
         *************************************************************************************************************/
        Z_ETHALON(new String[]
                {"", ""}),

        ;

        private final String text[];

        Texts(final String[] text) {
            this.text = text;
        }

        @Override
        public String toString() {
            try {
                return text[Settings.getInstance().getLanguage()];
            } catch (Exceptions e) {
                Terminator.terminate(e);
            }

            return "CRASH";
        }
    }
}
