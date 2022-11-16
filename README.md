# Topii Chat Android

## General

* Min Sdk 21
* Target Sdk 32
* JvmTarget = '1.8'
* Gradle = 7.2

## How to start application

* Clone project via https or ssh
* Checkout desired remote branch
* Build and install app on device/emulator via 'Run app'

## How to test chat

* Change account name to your random account name by changing property 'username' in '
  AccountUtil.kt'. Also you can change domain of XMPP server by changing property 'domain'(for test
  use - you can leave 'conversations.im')

```
object AccountUtil {

    fun createMockAccount(xmppConnectionService: XmppConnectionService): Account? {
        try {
            val username = "sampleaccount" //Account username
            val domain = "conversations.im" 
            ...
}
```

* Build application and log-in into Topii account
* Open Chats button and create chat account. You need to click 'Next', fill Captcha and if you do
  everything correctly - app will close.
* After restart of application you can navigate to chats and invite another XMPP account to start
  conversation.
* All prepare flow to test you can see in
  this https://drive.google.com/file/d/1fCQPxitHFo4GJr-hjLU-0o-qV8shbaRM/view?usp=share_link
  .

## Technology stack

* Kotlin
* Coroutines
* MVVM
* Dagger Hilt
* Navigation component