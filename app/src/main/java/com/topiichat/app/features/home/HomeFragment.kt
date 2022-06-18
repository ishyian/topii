package com.topiichat.app.features.home

//import android.os.Bundle
//import android.view.View
//import com.topiichat.android.sdk.TopiiChatClient
//import com.topiichat.android.sdk.listener.*
//import com.topiichat.android.sdk.models.*
import com.topiichat.app.R
//import com.topiichat.app.core.navigation.Navigator
import com.topiichat.app.core.platform.BaseFragment
//import javax.inject.Inject


class HomeFragment : BaseFragment(){
//    , OnConnListener, OnUserListener, OnAdvanceMsgListener, OnConversationListener, OnFriendshipListener,
//    OnGroupListener {
//
//    @Inject
//    lateinit var navigator: Navigator

    override fun layoutId() = R.layout.fragment_loader
//    /// api address
//    val IP_API = "http://43.128.5.63:10000"
//
//    /// websocket address
//    val IP_WS = "ws://43.128.5.63:17778"
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        initTopiiSDK()
//
//    }
//
//    private fun initTopiiSDK() {
//        val client: TopiiChatClient = TopiiChatClient.getInstance()
//        val path: String = ""
//        client.initSDK(IP_API, IP_WS, path, 6, "cos",this
//            )
//    }
//
//
//    override fun onConnectFailed(code: Long, error: String?) {
//        println("=======onConnectFailed=================")
//    }
//
//    override fun onConnectSuccess() {
//        println("===========onConnectSuccess=============")
//    }
//
//    override fun onConnecting() {
//        println("==========onConnecting==============")
//    }
//
//    override fun onKickedOffline() {
//        println("==========onKickedOffline==============")
//    }
//
//    override fun onSelfInfoUpdated(info: UserInfo?) {
//        println("==========onSelfInfoUpdated==============")
//    }
//
//    override fun onUserTokenExpired() {
//        println("=========onUserTokenExpired===============")
//    }
//
//    override fun onRecvNewMessage(msg: Message?) {
//        println("=========onRecvNewMessage===============")
//    }
//
//    override fun onRecvC2CReadReceipt(list: List<ReadReceiptInfo?>?) {
//        println("========onRecvC2CReadReceipt================")
//    }
//
//    override fun onRecvGroupMessageReadReceipt(list: List<ReadReceiptInfo?>?) {}
//
//    override fun onRecvMessageRevoked(msgId: String?) {
//        println("=======onRecvMessageRevoked=================")
//    }
//
//    override fun onConversationChanged(list: List<ConversationInfo?>?) {
//        println("========onConversationChanged================")
//    }
//
//    override fun onNewConversation(list: List<ConversationInfo?>?) {
//        println("==========onNewConversation==============")
//    }
//
//    override fun onSyncServerFailed() {
//        println("========onSyncServerFailed================")
//    }
//
//    override fun onSyncServerFinish() {
//        println("=========onSyncServerFinish===============")
//    }
//
//    override fun onSyncServerStart() {
//        println("=========onSyncServerStart===============")
//    }
//
//    override fun onTotalUnreadMessageCountChanged(i: Int) {
//        println("==========onTotalUnreadMessageCountChanged==============")
//    }
//
//
//    fun onGetConversation(view: View?) {
//        TopiiChatClient.getInstance().conversationManager.getAllConversationList(object :
//            OnBase<List<ConversationInfo?>?> {
//            override fun onError(code: Int, error: String) {}
//
//            override fun onSuccess(data: List<ConversationInfo?>?) {
//                TODO("Not yet implemented")
//            }
//        })
//    }
//
//    fun onLogin(view: View?) {
//        println("============login============")
//        TopiiChatClient.getInstance().login(
//            object : OnBase<String?> {
//                override fun onError(code: Int, error: String) {}
//
//                override fun onSuccess(data: String?) {
//                    TODO("Not yet implemented")
//                }
//            },
//            "13900000000",
//            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJVSUQiOiIxMzkwMDAwMDAwMCIsIlBsYXRmb3JtIjoiQW5kcm9pZCIsImV4cCI6MTk2Mzk3MjE0NCwibmJmIjoxNjQ4NjEyMTQ0LCJpYXQiOjE2NDg2MTIxNDR9.BEqWSNHV4P6d9NrA1Ji_kyHx1WnZY7R1Iket_6Oqpns"
//        )
//    }
//
//    fun onGetMessageHistory(view: View?) {}
//
//     fun onSendMsgTest(view: View?) {}
//
//    override fun onBlacklistAdded(u: BlacklistInfo?) {}
//
//    override fun onBlacklistDeleted(u: BlacklistInfo?) {}
//
//    override fun onFriendApplicationAccepted(u: FriendApplicationInfo?) {}
//
//    override fun onFriendApplicationAdded(u: FriendApplicationInfo?) {}
//
//    override fun onFriendApplicationDeleted(u: FriendApplicationInfo?) {}
//
//    override fun onFriendApplicationRejected(u: FriendApplicationInfo?) {}
//
//    override fun onFriendInfoChanged(u: FriendInfo?) {}
//
//    override fun onFriendAdded(u: FriendInfo?) {}
//
//    override fun onFriendDeleted(u: FriendInfo?) {}
//
//    override fun onGroupApplicationAccepted(info: GroupApplicationInfo?) {}
//
//    override fun onGroupApplicationAdded(info: GroupApplicationInfo?) {}
//
//    override fun onGroupApplicationDeleted(info: GroupApplicationInfo?) {}
//
//    override fun onGroupApplicationRejected(info: GroupApplicationInfo?) {}
//
//    override fun onGroupInfoChanged(info: GroupInfo?) {}
//
//    override fun onGroupMemberAdded(info: GroupMembersInfo?) {}
//
//    override fun onGroupMemberDeleted(info: GroupMembersInfo?) {}
//
//    override fun onGroupMemberInfoChanged(info: GroupMembersInfo?) {}
//
//    override fun onJoinedGroupAdded(info: GroupInfo?) {}
//
//    override fun onJoinedGroupDeleted(info: GroupInfo?) {}
}