# QYAndroidMqttV5

#### 介绍
qymqttv5 版本

#### 软件架构
mqtt 5.0版本

#### 使用
```
mqttV5 = MqttV5.Builder()
.setUserContext(context)
.setClientId(MqttConfig.mqttClientId)
.setUserName(MqttConfig.mqttUserName)
.setPassword(MqttConfig.mqttUserPw)
.setSubscribeTopics(arrayListOf("testtopic/#"))
.setKeepAliveInterval(10)
.setMaxReconnectDelay(3000)
.setSessionExpiryInterval(24*60*60)
.setSubscribeQos(MqttV5.QOS_REPEAT)
.setPublishQos(MqttV5.QOS_REPEAT)
.setPublishMsgRetained(true)
.setCleanStart(false)
//.setSocketFactory(SSLUtil.getSSL())
.setHttpsHostnameVerificationEnabled(false)
//.setSslHostnameVerifier { hostname, session -> true }
.setIReceiveActionListener { topic, msg ->
try {

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } //设置接收消息的监听
            .setIMqttStatusListener(object : IMqttStatusListener {
                override fun connectSuccess(asyncActionToken: IMqttToken) {
                    Log.d(TAG, "external receive 连接成功")
                }

                override fun connectFail(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.d(TAG, "external receive 连接失败$exception")
                }

                override fun unSubscribeSuccess(iMqttToken: IMqttToken) {
                    Log.d(TAG, "external receive 取消订阅成功:$iMqttToken")
                }

                override fun unSubscribeFail(iMqttToken: IMqttToken?, throwable: Throwable?) {
                    Log.d(TAG, "external receive 取消订阅失败$throwable")
                }

                override fun subscribeSuccess(iMqttToken: IMqttToken) {
                    Log.d(TAG, "external receive 订阅成功:$iMqttToken")
                }

                override fun subscribeFail(iMqttToken: IMqttToken, throwable: Throwable) {
                    Log.d(TAG, "external receive 订阅失败$throwable")
                }


                override fun connectComplete(reconnect: Boolean, serverURI: String) {
                    Log.d(TAG, "external receive 连接完成 reconnect=$reconnect serverURI=$serverURI")
                }

                override fun connectLost(cause: Throwable) {
                    Log.d(TAG, "external receive 连接丢失$cause")
                }
            }) //设置链接状态的监听
            .setIReceiveActionListener { topic, msg ->
                try {
                    Log.d(TAG, "external receive 接收: topic:$topic msg:$msg")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            .build()
        mqttV5?.connect("tcp://139.224.44.126:1883")

```


#### 说明

## 前言

最初学习MQTT3.1.1还是在2014年，转眼间6年过去了，这次学习MQTT5.0，一是为了跟上时代步伐，研究MQTT5.0的新特性，并理解从MQTT3.1.1升级MQTT5.0的必要性和时机；另一是为了重新温习一下MQTT协议，温故知新。

## 什么是MQTT？为什么使用MQTT？

### MQTT：The Standard for IoT messaging

MQTT是机器对机器(M2M)/物联网(IoT)连接协议。它被设计为一个极其轻量级的发布/订阅消息传输协议。对于需要较小代码占用空间和/或网络带宽非常宝贵的远程连接非常有用，是专为受限设备和低带宽、高延迟或不可靠的网络而设计。这些原则也使该协议成为新兴的“机器到机器”(M2M)或物联网(IoT)世界的连接设备，以及带宽和电池功率非常高的移动应用的理想选择。例如，它已被用于通过卫星链路与代理通信的传感器、与医疗服务提供者的拨号连接，以及一系列家庭自动化和小型设备场景。它也是移动应用的理想选择，因为它体积小，功耗低，数据包最小，并且可以有效地将信息分配给一个或多个接收器。

MQTT特点：

* 开发消息协议，简单易实现
* 发布/订阅模式，一对多消息发布
* 基于TCP/IP，提供有序、无损、双向连接
* 1字节固定包头，2字节心跳报文，最小化传输开销和协议转换，有效减少网络流量
* 消息QoS支持，可靠传输保证
### 为什么使用MQTT

#### 高效的和轻量级的

MQTT客户端非常小，可用于微控制器；MQTT协议包头小，可优化传输带宽

#### 双向通讯

MQTT协议支持设备到云、云到设备的双向通讯，易做到一对多广播式通讯

#### 支持百万级连接规模

#### 可靠消息传输

QoS0: 最多接收到1次消息（非可靠传输）

QoS1：至少接收到1次消息（可靠传输，允许重复接收）

QOS2：只会接收到1次消息（可靠传输，无重复）

#### 支持不可靠网络传输

多数物联网设备通过非可靠性蜂窝网络传输，MQTT的持久化连接技术，优化了重连的时长

#### 安全性

MQTT支持TLS加密传输技术，可使用现代用户认证协议（例如OAuth）对客户端鉴权

## MQTT5.0

### MQTT5.0：OASIS已经发布官方v5.0标准

MQTT 5.0 正式发布，这是一次重大的改进和升级，它的目的不仅仅是满足现阶段的行业需求，更是为行业未来的发展变化做了充足的准备。2019 年 3 月，MQTT 5.0 成为了新的 OASIS（结构化信息标准促进组织） 标准。

MQTT协议5.0版本在3.1.1版本的基础上增加了会话/消息延时功能、原因码、主题别名、in-flight流控、属性、共享订阅等功能，增加了用于增强认证的AUTH报文。

* 改进返回客户端的错误信息。现在，所有响应报文都将包含原因码和可选的人类易读的原因字符串。
* 规范通用模式，包括能力发现和请求响应等。
* 对共享订阅的协议支持，此前标准无共享订阅的内容，共享订阅由各个软件厂商自已定义，不具备通用性。
* 新的扩展机制，包括用户属性。
* 引入主题别名等新特性进一步减小传输开销
* 增加了会话过期间隔和消息过期间隔，用以改善老版本中 Clean Session 不够灵活的地方。
### MQTT5.0新特性

#### 增加属性Property

CONNECT，CONNACK，PUBLISH，PUBACK，PUBREC，PUBREL，PUBCOMP，SUBSCRIBE，SUBACK，UNSUBACK，DISCONNECT和AUTH报文可变报头的最后一部分是一组属性。CONNECT报文的遗嘱（Will）属性字段中也包含了一组可选的属性。

属性字段由属性长度和所有属性组成。

属性长度 Property Length

属性长度被编码为变长字节整数。属性长度不包含用于编码属性长度自身的字节数，但包含所有属性的长度。如果没有任何属性，必须由属性长度为零的字段来指示 [MQTT-2.2.2-1]。

属性 Property

一个属性包含一段数据和一个定义了属性用途和数据类型的标识符。标识符被编码为变长字节整数。任何控制报文，如果包含了：对于该报文类型无效的标识符，或者错误类型的数据，都是无效报文。收到无效报文时，服务端或客户端使用包含原因码0x81（无效报文）CONNACK或DISCONNECT报文进行错误处理。

*相关属性请参考协议原文。*

*非规范化说明：协议中的“使用UTF-8字符串表示”的内容，是内容长度+内容体两部分组成，内容长度被编码为变长字节整数。*

#### Clean Start 与 Session Expiry Interval

用于替代MQTTV3.1.1的Clean Session。对比：

MQTTV3.1.1：

* CleanSession=0，表示服务端必须使用与 Client ID 关联的会话来恢复与客户端的通信。如果不存在这样的会话，服务器必须创建一个新会话。客户端和服务器在断开连接后必须存储会话的状态。
* CleanSession=1，客户端和服务器必须丢弃任何先前的会话并创建一个新的会话。该会话的生命周期将和网络连接保持一致，其会话状态一定不能被之后的任何会话重用。
  MQTTV5.0：

* CleanStart=0，并且存在一个关联此客户端标识符的会话，服务端必须基于此会话的状态恢复与客户端的通信。如果不存在任何关联此客户端标识符的会话，服务端必须创建一个新的会话。
* CleanStart=1，客户端和服务端必须丢弃任何已存在的会话，并开始一个新的会话。
* Session Expiry Interval 以秒为单位，SEI=0或未指定，会话将在网络连接关闭时结束
* SEI=0XFFFFFFFF，则会话永不过期，网络连接关闭时，SEI大于0，则客户端和服务器必须存储会话状态；SEI可在DISCONNECT协议报文中被更改。
#### 消息过期间隔

MQTTV3.1.1：

无此时间设置，个人理解为永不过期。

MQTTV5.0：

如果消息过期间隔存在，四字节整数表示以秒为单位的应用消息（Application Message）生命周期。如果消息过期间隔（Message Expiry Interval）已过期，服务端还没开始向匹配的订阅者交付该消息，则服务端必须删除该订阅者的消息副本 [MQTT-3.3.2-5]。

如果消息过期间隔不存在，应用消息不会过期。

*非规范化说明：该处指代的“服务器还没开始向匹配的订阅者交付该消息”，指QoS>0时，客户端不在线或未按时回复ACK的情况。在MQTTV3.1.1版本，消息副本会一直存储在服务器端，并根据QoS的规则尝试发送；而MQTTV5.0，定义了消息的TTL，可以让客户端与服务器根据需要协商，减轻服务器压力。*

#### 原因码与 ACK

MQTTV3.1.1：

CONNACK有6个返回码；SUBACK有4个返回码。

MQTTV5.0：

将返回码改为原因码，并进行扩充，原因码小于0x80表示成功，大于0x80表示失败。CONNACK, PUBACK, PUBREC, PUBREL, PUBCOMP, DISCONNECT 和 AUTH 控制报文的原因码存在可变报头中。而 SUBACK 和 UNSUBACK 报文在有效载荷中包含了一张原因码的列表。

*具体列表参考协议原文档。*

#### 有效载荷标识与内容类型

MQTTV3.1.1：无此字段

MQTTV5.0：

* 有效载荷标识（Payload Format Indicator），表示Payload是否为UTF-8编码数据，1表示是，0表示未确定的字节。存在于CONNECT的遗嘱属性和PUBLISH的报文中。
* 内容类型（Content Type），同样的存在于CONNECT的遗嘱属性与PUBLISH的报文中，由消息收发方指定，消息传输和转发时不可修改。内容类型的一个比较典型的应用就是存放 MIME 类型，比如 text/plain 表示文本文件，audio/aac 表示音频文件。
#### Request/Response 模式

MQTTV3.1.1：无此模式

MQTTV5.0：

简单举例说明：

* MQTT客户端（请求方）会向TopicA发布请求（Request），其请求中会指定响应主题（TopicB）和对比数据（Correlation Data）；
* 任何订阅了TopicA的角色均会收到请求，并按需处理，然后回复对比数据（如有）至TopicB
* 也会存在多方订阅TopicB的情况，所有订阅了TopicB的角色均会收到响应数据，并处理。
#### 共享订阅

MQTTV3.1.1：无此功能，当多个客户端订阅同一个Topic时，所有客户端均会收到发送至该Topic的消息副本。

MQTTV5.0：共享订阅是 MQTT 5.0 协议引入的新特性，相当于是订阅端的负载均衡功能。

* 共享订阅的主题格式必须为：
```plain
$share/{ShareName}/{filter}
// $share前缀表示该主题为共享订阅主题
// {ShareName}是一个不包含"/"、"+"、"#"的字符串，所有消费者通过使用相同的ShareName表示共享该主题的订阅，消息每次只会发给订阅了该主题的某一个消费者
// {filter}该字符串的剩余部分与非共享订阅中的主题过滤器具有相同的语法和语义

```
*非规范化说明：*
>共享订阅在MQTT服务端的范围内定义，而不是在会话中定义。共享订阅的主题过滤器包含共享名，因此服务端可以有多个包含相同{过滤器}组件的共享订阅。通常，应用程序使用共享名表示共享同一个订阅的一组订阅会话。 示例：
>共享订阅`$share/consumer1/sport/tennis/+`和 `$share/consumer2/sport/tennis/+`是不同的共享订阅，因此可以被关联到不同的会话组。它们都与非共享订阅主题`sport/tennis/+`相匹配。
>如果一条消息被发布到匹配主题`sport/tennis/+`，则消息的副本仅发送给所有订阅`$share/consumer1/sport/tennis/+`的会话中的一个会话，也仅发送给所有订阅`$share/consumer2/sport/tennis/+`的会话中的一个会话。更多的副本将发送给所有对`sport/tennis/+`进行非共享订阅的客户端。
>共享订阅`$share/consumer1//finance`匹配非共享订阅主题`/finance`。
>注意，`$share/consumer1//finance”和“$share/consumer1/sport/tennis/+`是不同的共享订阅，尽管它们有相同的共享名。它们可能在某种程度上是相关的，但拥有相同的共享名并不意味着它们之间有某种关系。
#### 订阅标识符与订阅选项

订阅标识符：

MQTTV3.1.1： 无订阅标识符选项

MQTTV5.0：订阅者可以将订阅主题和一个特定字符串做关联映射，这样在收到消息的同时，也会收到该字符串。

因此，客户端可以建立订阅标识符与消息处理程序的映射，以在收到 PUBLISH 报文时直接通过订阅标识符将消息定向至对应的消息处理程序，这会远远快于通过主题匹配来查找消息处理程序的速度。

*非规范化说明：*

>由于 SUBSCRIBE 报文支持包含多个订阅，因此可能出现多个订阅关联到同一个订阅标识符的情况。即便是分开订阅，也可能出现这种情况，但这是被允许的，只是用户应当意识到这样使用可能引起的后果。根据客户端的实际订阅情况，最终客户端收到的 PUBLISH 报文中可能包含多个订阅标识符，这些标识符可能完全不同，也可能有些是相同的，以下是几种常见的情况：
>客户端订阅主题 a 并指定订阅标识符为 1，订阅主题 b 并指定订阅标识符为 2。由于使用了不同的订阅标识符，主题为 a 和 b 的消息能够被定向至不同的消息处理程序。
>客户端订阅主题 a 并指定订阅标识符为 1，订阅主题 b 并指定订阅标识符为 1。由于使用了相同的订阅标识符，主题为 a 和 b 的消息都将被定向至同一个消息处理程序。
>客户端订阅主题 a/+ 并指定订阅标识符为 1，订阅主题 a/b 并指定订阅标识符为 1。主题为 a/b 的 PUBLISH 报文将会携带两个相同的订阅标识符，对应的消息处理程序将被触发两次。
>客户端订阅主题 a/+ 并指定订阅标识符为 1，订阅主题 a/b 并指定订阅标识符为 2。主题为 a/b 的 PUBLISH 报文将会携带两个不同的订阅标识符，一个消息将触发两个不同的消息处理程序。
订阅选项：

MQTTV3.1.1：仅支持QoS

MQTTV5：支持QoS、No Local、Retain As Publish、Retain Handling

* No Local：在 MQTT v3.1.1 中，如果你订阅了自己发布消息的主题，那么你将收到自己发布的所有消息。而在 MQTT v5 中，如果你在订阅时将此选项设置为 1，那么服务端将不会向你转发你自己发布的消息。
* Retain As Publish：
>如果发布保留（Retain As Published）订阅选项被设置为0，服务端在转发应用消息时必须将保留标志设置为0，而不管收到的PUBLISH报文中保留标志位如何设置的 [MQTT-3.3.1-12]。
>如果发布保留（Retain As Published）订阅选项被设置为1，服务端在转发应用消息时必须将保留标志设置为与收到的PUBLISH消息中的保留标志位相同 [MQTT-3.3.1-13]。
*非规范化说明：这一选项用来指定服务端向客户端转发消息时是否要保留其中的 RETAIN 标识，注意这一选项不会影响保留消息中的 RETAIN 标识。因此当 Retain As Publish 选项被设置为 0 时，客户端直接依靠消息中的 RETAIN 标识来区分这是一个正常的转发消息还是一个保留消息，而不是去判断消息是否是自己订阅后收到的第一个消息（转发消息甚至可能会先于保留消息被发送，视不同 Broker 的具体实现而定）。*

* Retain Handling
>Retain Handling 等于 0，只要客户端订阅成功，服务端就发送保留消息。
>Retain Handling 等于 1，客户端订阅成功且该订阅此前不存在，服务端才发送保留消息。毕竟有些时候客户端重新发起订阅可能只是为了改变一下 QoS，并不意味着它想再次接收保留消息。
>Retain Handling 等于 2，即便客户订阅成功，服务端也不会发送保留消息。
#### 主题别名(Topic Alias)

类似HTTP2的头部压缩效果，当然，没有同HPACK那么复杂的东西。

我们知道，PUBLISH消息的时候，需要携带 topic和message，其中topic往往是固定的，那么我们只需要第一次发送完整的 topic，并且通过Property中携带Topic Alias告知对端下次这个PUBLISH的topic会使用Topic Alias中的值代替，Topic Alias的值是一个整数类型的值。目的是为了压缩Publish的报文长度。

client 通过 CONNECT 中 Topic Alias Maximum 告知 Server自己能处理的最多的 Topic Alias 个数。

Server 通过 CONNACK中 Topic Alias Maximum 告知 Client自己能处理的最多的 Topic Alias 个数。

如果当前PUBLISH消息的topic长度不为0，那么接受方需要解析 Topic Alias 中的值，并且 将topic和该值进行映射。

如果当前PUBLISH消息的topic为0，那么接受方需要解析 Topic Alias 中的值，用该值去查找对应的topic。

#### 流量控制

允许客户端和服务端分别指定未完成的可靠消息（QoS>0）的数量。发送端可以暂停发送此类消息以保持消息数量低于配额。这被用于限制可靠消息的速率和某一时刻的传输中（in-flight）消息数量。

在 MQTT v5 中，发送端会有一个初始的发送配额，每当它发送一个 QoS 大于 0 的 PUBLISH 报文，发送配额就相应减一，而每当收到一个响应报文（PUBACK、PUBCOMP 或 PUBREC），发送配额就会加一。如果接收端没有及时响应，导致发送端的发送配额减为 0，发送端应当停止发送所有 QoS 大于 0 的 PUBLISH 报文直至发送配额恢复。我们可以将其视为变种的令牌桶算法，它们之间的区别仅仅是增加配额的方式从以固定速率增加变成了按实际收到响应报文的速率增加。

Receive Maximum 属性

为了支持流量控制，MQTT v5 新增了一个 Receive Maximum 属性，它存在于 CONNECT 报文与 CONNACK 报文，表示客户端或服务端愿意同时处理的 QoS 为 1 和 2 的 PUBLISH 报文最大数量，即对端可以使用的最大发送配额。如果接收端已收到但未发送响应的 QoS 大于 0 的 PUBLISH 报文数量超过 Receive Maximum 的值，接收端将断开连接避免受到更严重的影响。

#### 用户属性

自定义属性，可以添加两端约定的数据。例如可以加入类似HTTP的 "Header:value"信息。MQTT本身没有类似HTTP的HOST信息，我们可以使用User Property特性让MQTT支持。

MQTT为大多数报文添加用户属性。PUBLISH报文的用户属性由客户端应用程序定义。PUBLISH报文和遗嘱报文的用户属性由服务端转发给应用消息的接收端。CONNECT，SUBSCRIBE和UNSUBSCRIBE报文的用户属性由服务端实现定义。CONNACK，PUBACK，PUBREC，PUBREL，PUBCOMP，SUBACK，UNSUBACK和AUTH报文的用户属性由发送端定义，且对发送端具有唯一性。MQTT规范不定义用户属性的意义。

#### 增强认证

简单认证

即通过用户名密码的认证方法。

该方法对于客户端和服务器的算力占用都很低，对于安全性要求不是那么高，计算资源紧张的业务，可以使用简单认证。但是，在基于用户名和密码这种简单认证模型的协议中，客户端和服务器都知道一个用户名对应一个密码。在不对信道进行加密的前提下，无论是直接使用明文传输用户名和密码，还是给密码加个哈希的方法都很容易被攻击。

增强认证

>增强认证
>基于更强的安全性考虑，MQTT v5 增加了新特性增强认证，增强认证包含质询/响应风格的认证，可以实现对客户端和服务器的双向认证，服务器可以验证连接的客户端是否是真正的客户端，客户端也可以验证连接的服务器是否是真正的服务器，从而提供了更高的安全性。
>增强认证依赖于认证方法和认证数据来完成整个认证过程，在增强认证中，认证方法通常为 SASL（ Simple Authentication and Security Layer ) 机制，使用一个注册过的名称便于信息交换。但是，认证方法不限于使用已注册的 SASL 机制，服务器和客户端可以约定使用任何质询 / 响应风格的认证。
>认证方法
>认证方法是一个 UTF-8 的字符串，用于指定身份验证方式，客户端和服务器需要同时支持指定的认证方法。客户端通过在 CONNECT 报文中添加认证方法字段来启动增强认证，增强认证过程中客户端和服务器交换的报文都需要包含认证方法字段，并且认证方法必须与 CONNECT 报文保持一致。
>认证数据
>认证数据是二进制信息，用于传输加密机密或协议步骤的多次迭代。认证数据的内容高度依赖于认证方法的具体实现。
>增强认证流程
>相比于依靠 CONNECT 报文和 CONNACK 报文一次交互的简单认证，增强认证需要客户端与服务器之间多次交换认证数据，因此，MQTT v5 新增了 AUTH 报文来实现这个需求。增强认证是基于 CONNECT 报文、CONNACK 报文以及 AUTH 报文三种 MQTT 报文类型实现的，三种报文都需要携带认证方法与认证数据达成双向认证的目的。
>要开启增强认证流程，需要客户端向服务器发送包含了认证方法字段的 CONNECT 报文，服务器收到了 CONNECT 报文后，它可以与客户端通过 AUTH 报文继续交换认证数据，在认证完成后向客户端发送 CONNACK 报文。
>*SCRAM 认证非规范示例*
>客户端到服务端: CONNECT 认证方法="SCRAM-SHA-1"，认证数据=client-first-data
>服务端到客户端: AUTH 原因码=0x18，认证方法="SCRAM-SHA-1"，认证数据=server-first-data
>客户端到服务端: AUTH 原因码=0x18，认证方法="SCRAM-SHA-1"，认证数据=client-final-data
>服务端到客户端: CONNACK 原因码=0，认证方法="SCRAM-SHA-1"，认证数据=server-final-data
>*Kerberos 认证非规范示例*
>客户端到服务端: CONNECT 认证方法="GS2-KRB5"
>服务端到客户端: AUTH 原因码=0x18，认证方法="GS2-KRB5"
>客户端到服务端: AUTH 原因码=0x18，认证方法="GS2-KRB5"，认证数据=initial context token
>服务端到客户端: AUTH 原因码=0x18，认证方法="GS2-KRB5"，认证数据=reply context token
>客户端到服务端: AUTH 原因码=0x18，认证方法="GS2-KRB5"
>服务端到客户端: CONNACK 原因码=0，认证方法="GS2-KRB5"，认证数据=outcome of authentication
>在增强认证的过程中，客户端与服务器需要进行多次认证数据的交换，每次交换都需要通过认证算法对认证数据进行加解密的计算，所以它需要更多的计算资源以及更稳定的网络环境，因此它并不适合算力薄弱、网络波动大的边缘设备，而支持增强认证的 MQTT 服务器 也需要准备更多的计算资源来应对大量的连接。
>重新认证
>增强认证完成之后，客户端可以在任意时间通过发送 AUTH 报文发起重新认证，重新认证开始后，同增强认证一样，客户端与服务器通过交换 AUTH 报文来交换认证数据，直到服务器向客户端发送原因码为 0x00（ 成功) 的 AUTH 报文表示重新认证成功。需要注意的是，重新认证的认证方法必须与增强认证一致。
>在重新认证的过程中，客户端和服务器的其他报文流可以继续使用之前的认证。
#### Reason String

所有的ACK以及DISCONNECT 都可以携带 Reason String属性告知对方一些特殊的信息，一般来说是ACK失败的情况下会使用该属性告知对端为什么失败，可用来弥补Reason Code信息不够。

#### Server redirection

Server可以发送 CONNACK 或者 DISCONNECT，其 Reason Codes 可以是0x9c或者0x9d，表示Client需要往另一个Server发送请求。

0x9C 类似 HTTP 的 302, 0x9d 类似 HTTP的 301。

CONNACK 或者 DISCONNECT 可以通过 Property携带Server redirection，其值可以告诉Client往哪个Server发送请求，类似HTTP的"Location"首部。

#### Maximum Packet Size

顾名思义，单个 MQTT控制报文 的大小，如果不携带，表示不限制。

这个大小指整个 MQTT控制报文 的大小。对端如果发现将发送的包大于该大小，就默默丢弃，不关闭连接。如果自己收到超过自己通告的Maximum Packet Size需要关闭连接。

#### 可选的服务端功能可用性

提供定义一组服务端不允许的功能，并告知客户端的机制。可以使用这种方式指定的功能包括：最大QoS等级，保留可用，通配符订阅可用，订阅标识符可用和共享订阅可用。客户端使用服务端通知了（不可用）的功能将造成错误。

在早期版本的MQTT协议中，服务端没有实现的功能通过未授权告知客户端。当客户端使用其中一种（不可用的）功能时，此功能允许服务端告知客户端，并添加特定的原因码。

#### 遗嘱延迟

提供指定遗嘱消息在连接中断后延时发送的能力。设计此特性是为了在会话的连接重建的情况下不发送遗嘱消息。此特性允许连接短暂中断而不通知其他客户端。

#### 服务端保持连接

允许服务端指定其希望客户端使用的保持连接值。此特性允许服务端设置最大允许的保持连接值并被客户端使用。

#### 分配客户标识符

服务端分配了客户标识符的情况下，向客户端返回此客户标识符。服务端分配客户标识符只能用于新开始标志为1的连接。


---


### 参考文献

[https://blog.csdn.net/mrpre/article/details/87267400](https://links.jianshu.com/go?to=https%3A%2F%2Fblog.csdn.net%2Fmrpre%2Farticle%2Fdetails%2F87267400)

[https://www.emqx.cn/blog/introduction-to-mqtt-5](https://links.jianshu.com/go?to=https%3A%2F%2Fwww.emqx.cn%2Fblog%2Fintroduction-to-mqtt-5)








#### 参与贡献

1.  puy

