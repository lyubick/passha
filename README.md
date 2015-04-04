# pasSHA
## Basic concept.
The purpose of this Project is to create perfectly Safe Password Manager and Generator, that will allow User, while remembering only one Password, maintain different Passwords for all the WEB and beyond.
The main idea of a Project is to create Tool that will NOT keep password in any form. Most of the existing tools keep generated Passwords in encrypted files. Not keeping the Password in a direct way (crypted or not) gives User assurance, that even if they lose files and this file will be decrypted NO information will be compromised

### How we avoid keeping the Password directly ?
**Step 1:** Tool uses SHA-512 to get Hash Value from User Password.

**Step 2:** Tool generates random number "A" from 10 to 255.

**Step 3:** Hash Value will be put through same SHA-512 for "A" times. [Salt](http://en.wikipedia.org/wiki/Salt_%28cryptography%29) will be added to avoid overlapping.

**Step 4:** Final Hash Value is used to generate Password.

**Finaly:** Only value "A" should be kept, to reproduce the Password.

### Are there additional secure measures?
Yes! User data is encrypted using ~RSA-1024 (p,q,e are 512 bits long).

### What rules can be applied to Generated Passwords?
By default Password will contain numbers and lower case characters, additionally one can add Special Characters and/or Upper case characters.

### How strong are Genrated Passwords ?
Generated Passwords are not trivial. For instance Steam recognized password as Strong.

### What languages does Tool supports ?
Currently Tool support English and Russian languages.
