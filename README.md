pos 系统开发指南
===

Database import step:
1. default-create.sql
2. data.sql
3. view.sql (Just now create 1st sql for report 1)

Android接口开发说明请参见连接：https://github.com/superleo-cn/pos/wiki

1. 密码生成策略:
    * 第一次Android客户端必须连接服务器，提交[用户名],[密码]以及[MAC地址]提交到服务器进行验证。如果验证成功，则生成一个本地密码，为Android本地登录密码。
    * 以后每次登录时，只需要验证本地密码即可。服务器和本地密码是分开独立存放的，不再做服务器验证。
2. 同步策略:
    * 每次提交交易数据到服务器的时，必须同时提交本机的MAC地址进行验证；如果MAC验证不通过，则同步失败。
    * 同步的请求如果因为网络或者服务器错误而失败，则Android需要继续存放到本地。等网络连接成功，继续提交。
3. 重置密码策略：
    * 重置密码目前只允许超级管理员操作。
    * 重置密码时，发送[用户名]和[MAC地址]提交到服务器进行验证，验证成功后，生成一个密码给本机使用。
4. 数据同步策略：
    * 重置密码目前只允许超级管理员操作。
    * 管理员点击相应的同步模块，Android系统删除本机数据，从服务器全部重新下载到本地。

