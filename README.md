# pasSHA
## Overview.
The purpose of this Project is to create perfectly safe **Password Manager and Generator** - *pasSHA*.
*pasSHA* allows User, while remembering **only one Password**, maintain different Passwords for all the WEB and beyond.
The main idea is that *pasSHA* will **NOT keep passwords** in any form, but generate them *on-the-fly*.
Existing alternative tools keep generated Passwords in encrypted files.
*pasSHA* is not keeping Passwords in any way (encrypted or not) and that gives assurance, if a file compromised and decrypted **NO information will be disclosed**.

### How *pasSHA* avoids keeping the Password directly?
**Step 1:** Tool uses **SHA-512** to get Hash Value from **User Password** (aka **Primary**).

**Step 2:** Tool generates random number **A** from *10* to *255*.

**Step 3:** Hash Value put through SHA-512 for **A** times. [Salt](http://en.wikipedia.org/wiki/Salt_%28cryptography%29) added to avoid overlapping.

**Step 4:** Final Hash Value used to generate all Passwords (aka **Secondary**).

**Finally:** Only value **A** kept in User file, to reproduce a key.

### How secure is it?
[This](https://www.youtube.com/watch?v=S9JGmA5_unY&ab_channel=3Blue1Brown) video shows how secure SHA-256 is. *pasSHA* uses next version, so more security.

### Are there additional secure measures?
Yes! User data encrypted using RSA-4096 (in version 3.0).

### What rules can be applied to Generated Passwords?
By default, Password will contain numbers and lower case characters. Additionally, user can switch on $peci@l Characters and UPPER CASE Characters.

### How strong are Generated Passwords ?
Generated Passwords are non-trivial. Most of the WEB sites, applications etc. recognizes passwords as strong.

### What languages does Tool supports ?
Currently, Tool support only English language.