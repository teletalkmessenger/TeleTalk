package org.telegram.hojjat.tmp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.PhotoViewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class ActivityTrends extends Activity implements PhotoViewer.PhotoViewerProvider, MessageReceiver {
    protected TLRPC.Chat currentChat;
    RecyclerListView chatListView;
    ArrayList<MessageObject> messages;
    ChatActivityAdapter chatAdapter;

    LinearLayoutManager chatLayoutManager;

    private static final String TAG = "HOJJAT_ActivityTrends";

    public static final String EXT = ".msg";

    public static final String CHATS_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download";
    public static final String TRENDS_FILE = "trends.msg";

    @Override
    public void messageReceived(TLRPC.Message msg) {
        messages.add(new MessageObject(msg, null, true));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().closePhoto(true, false);
        } else
            super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trends);
        loadMessages();
//        chatListView = (RecyclerListView) findViewById(R.id.listView);
        FrameLayout root = (FrameLayout) findViewById(R.id.root);
        chatListView = new RecyclerListView(this);
        chatLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        chatLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        chatLayoutManager.setStackFromEnd(true);
        chatListView.setLayoutManager(chatLayoutManager);
        chatAdapter = new ChatActivityAdapter(this);
        chatListView.setAdapter(chatAdapter);
        chatListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        chatListView.setOnInterceptTouchListener(new RecyclerListView.OnInterceptTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(MotionEvent event) {
                return false;
            }
        });
        root.addView(chatListView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        chatAdapter.notifyDataSetChanged();
    }

    private void loadMessages() {
        messages = new ArrayList<>();
        try {
            File dir = new File(ActivityTrends.CHATS_DIR);
            dir.mkdirs();
            File file = new File(dir, ActivityTrends.TRENDS_FILE);
            if (!file.exists()) return;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.length() > 1) {
                    String[] split = line.split(":::");
                    MessageHelper.getInstance().getMessage(split[0], new Integer(split[1]), this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


//        File dir = new File(ActivityTrends.CHATS_DIR);
//        dir.mkdirs();
//        File[] files = dir.listFiles();
//        for (File file : files) {
//            addMessage(file);
//        }

//        MessageHelper.getInstance().getMessage("zhuanchannel", new Integer(2323), this);
    }


    private void addMessage(File f) {
        try {
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream is = new ObjectInputStream(fis);
            TLRPC.Message m = (TLRPC.Message) is.readObject();
            messages.add(new MessageObject(m, null, true));
            Log.d(TAG, "addMessage: file Added. size: " + fis.getChannel().size());
            is.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updatePhotoAtIndex(int index) {

    }

    @Override
    public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
        int count = chatListView.getChildCount();

        for (int a = 0; a < count; a++) {
            ImageReceiver imageReceiver = null;
            View view = chatListView.getChildAt(a);
            if (view instanceof ChatMessageCell) {
                if (messageObject != null) {
                    ChatMessageCell cell = (ChatMessageCell) view;
                    MessageObject message = cell.getMessageObject();
                    if (message != null && message.getId() == messageObject.getId()) {
                        imageReceiver = cell.getPhotoImage();
                    }
                }
            } else if (view instanceof ChatActionCell) {
                ChatActionCell cell = (ChatActionCell) view;
                MessageObject message = cell.getMessageObject();
                if (message != null) {
                    if (messageObject != null) {
                        if (message.getId() == messageObject.getId()) {
                            imageReceiver = cell.getPhotoImage();
                        }
                    } else if (fileLocation != null && message.photoThumbs != null) {
                        for (int b = 0; b < message.photoThumbs.size(); b++) {
                            TLRPC.PhotoSize photoSize = message.photoThumbs.get(b);
                            if (photoSize.location.volume_id == fileLocation.volume_id && photoSize.location.local_id == fileLocation.local_id) {
                                imageReceiver = cell.getPhotoImage();
                                break;
                            }
                        }
                    }
                }
            }

            if (imageReceiver != null) {
                int coords[] = new int[2];
                view.getLocationInWindow(coords);
                PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
                object.viewX = coords[0];
                object.viewY = coords[1] - AndroidUtilities.statusBarHeight;
                object.parentView = chatListView;
                object.imageReceiver = imageReceiver;
                object.thumb = imageReceiver.getBitmap();
                object.radius = imageReceiver.getRoundRadius();
                if (view instanceof ChatActionCell && currentChat != null) {
                    object.dialogId = -currentChat.id;
                }
                return object;
            }
        }
        return null;
    }

    @Override
    public Bitmap getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
        return null;
    }

    @Override
    public void willSwitchFromPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
    }

    @Override
    public void willHidePhotoViewer() {}

    @Override
    public boolean isPhotoChecked(int index) {
        return false;
    }

    @Override
    public void setPhotoChecked(int index) {
    }

    @Override
    public boolean cancelButtonPressed() {
        return true;
    }

    @Override
    public void sendButtonPressed(int index) {
    }

    @Override
    public int getSelectedCount() {
        return 0;
    }

    public class ChatActivityAdapter extends RecyclerView.Adapter {

        private Context mContext;
        private int rowCount;
        private int messagesStartRow;
        private int messagesEndRow;

        public ChatActivityAdapter(Context context) {
            mContext = context;
        }

        private class Holder extends RecyclerView.ViewHolder {

            public Holder(View itemView) {
                super(itemView);
            }
        }

        public void updateRows() {
            rowCount = 0;
            if (!messages.isEmpty()) {
                messagesStartRow = rowCount;
                rowCount += messages.size();
                messagesEndRow = rowCount;
            } else {
                messagesStartRow = -1;
                messagesEndRow = -1;
            }
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public long getItemId(int i) {
            return RecyclerListView.NO_ID;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder: viewType " + viewType);
            View view = null;
            if (viewType == 0) {
                view = new ChatMessageCell(mContext);
                ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                chatMessageCell.setDelegate(new ChatMessageCell.ChatMessageCellDelegate() {
                    @Override
                    public void didPressedShare(ChatMessageCell cell) {
//                        if (getParentActivity() == null) {
//                            return;
//                        }
//                        if (chatActivityEnterView != null) {
//                            chatActivityEnterView.closeKeyboard();
//                        }
//                        showDialog(new ShareAlert(mContext, cell.getMessageObject(), ChatObject.isChannel(currentChat) && !currentChat.megagroup && currentChat.username != null && currentChat.username.length() > 0));
                        Toast.makeText(parent.getContext(), "Share", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public boolean needPlayAudio(MessageObject messageObject) {
//                        if (messageObject.isVoice()) {
//                            boolean result = MediaController.getInstance().playAudio(messageObject);
//                            MediaController.getInstance().setVoiceMessagesPlaylist(result ? createVoiceMessagesPlaylist(messageObject, false) : null, false);
//                            return result;
//                        } else if (messageObject.isMusic()) {
//                            return MediaController.getInstance().setPlaylist(messages, messageObject);
//                        }
                        return false;
                    }

                    @Override
                    public void didPressedChannelAvatar(ChatMessageCell cell, TLRPC.Chat chat, int postId) {
//                        if (actionBar.isActionModeShowed()) {
//                            processRowSelect(cell);
//                            return;
//                        }
//                        if (chat != null && chat != currentChat) {
//                            Bundle args = new Bundle();
//                            args.putInt("chat_id", chat.id);
//                            if (postId != 0) {
//                                args.putInt("message_id", postId);
//                            }
//                            if (MessagesController.checkCanOpenChat(args, ChatActivity.this)) {
//                                presentFragment(new ChatActivity(args), true);
//                            }
//                        }
                    }

                    @Override
                    public void didPressedOther(ChatMessageCell cell) {
//                        createMenu(cell, true);
                        Toast.makeText(parent.getContext(), "Action", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void didPressedUserAvatar(ChatMessageCell cell, TLRPC.User user) {
//                        if (actionBar.isActionModeShowed()) {
//                            processRowSelect(cell);
//                            return;
//                        }
//                        if (user != null && user.id != UserConfig.getClientUserId()) {
//                            Bundle args = new Bundle();
//                            args.putInt("user_id", user.id);
//                            ProfileActivity fragment = new ProfileActivity(args);
//                            fragment.setPlayProfileAnimation(currentUser != null && currentUser.id == user.id);
//                            presentFragment(fragment);
//                        }
                        Toast.makeText(parent.getContext(), "Action", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void didPressedBotButton(ChatMessageCell cell, TLRPC.KeyboardButton button) {
//                        if (getParentActivity() == null || bottomOverlayChat.getVisibility() == View.VISIBLE && !(button instanceof TLRPC.TL_keyboardButtonCallback) && !(button instanceof TLRPC.TL_keyboardButtonUrl)) {
//                            return;
//                        }
//                        chatActivityEnterView.didPressedBotButton(button, cell.getMessageObject(), cell.getMessageObject());
                        Toast.makeText(parent.getContext(), "Action", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void didPressedCancelSendButton(ChatMessageCell cell) {
//                        MessageObject message = cell.getMessageObject();
//                        if (message.messageOwner.send_state != 0) {
//                            SendMessagesHelper.getInstance().cancelSendingMessage(message);
//                        }
                        Toast.makeText(parent.getContext(), "Action", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void didLongPressed(ChatMessageCell cell) {
//                        createMenu(cell, false);
                        Toast.makeText(parent.getContext(), "Action", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public boolean canPerformActions() {
//                        return actionBar != null && !actionBar.isActionModeShowed();
                        return true;
                    }

                    @Override
                    public void didPressedUrl(MessageObject messageObject, final ClickableSpan url, boolean longPress) {
//                        if (url == null) {
//                            return;
//                        }
//                        if (url instanceof URLSpanUserMention) {
//                            TLRPC.User user = MessagesController.getInstance().getUser(Utilities.parseInt(((URLSpanUserMention) url).getURL()));
//                            if (user != null) {
//                                MessagesController.openChatOrProfileWith(user, null, ChatActivity.this, 0);
//                            }
//                        } else if (url instanceof URLSpanNoUnderline) {
//                            String str = ((URLSpanNoUnderline) url).getURL();
//                            if (str.startsWith("@")) {
//                                MessagesController.openByUserName(str.substring(1), ChatActivity.this, 0);
//                            } else if (str.startsWith("#")) {
//                                if (ChatObject.isChannel(currentChat)) {
//                                    openSearchWithText(str);
//                                } else {
//                                    DialogsActivity fragment = new DialogsActivity(null);
//                                    fragment.setSearchString(str);
//                                    presentFragment(fragment);
//                                }
//                            } else if (str.startsWith("/")) {
//                                if (URLSpanBotCommand.enabled) {
//                                    chatActivityEnterView.setCommand(messageObject, str, longPress, currentChat != null && currentChat.megagroup);
//                                }
//                            }
//                        } else {
//                            final String urlFinal = ((URLSpan) url).getURL();
//                            if (longPress) {
//                                BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
//                                builder.setTitle(urlFinal);
//                                builder.setItems(new CharSequence[]{LocaleController.getString("Open", R.string.Open), LocaleController.getString("Copy", R.string.Copy)}, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, final int which) {
//                                        if (which == 0) {
//                                            Browser.openUrl(getParentActivity(), urlFinal, inlineReturn == 0);
//                                        } else if (which == 1) {
//                                            AndroidUtilities.addToClipboard(urlFinal);
//                                        }
//                                    }
//                                });
//                                showDialog(builder.create());
//                            } else {
//                                if (url instanceof URLSpanReplacement) {
//                                    showOpenUrlAlert(((URLSpanReplacement) url).getURL());
//                                } else if (url instanceof URLSpan) {
//                                    Browser.openUrl(getParentActivity(), urlFinal, inlineReturn == 0);
//                                } else {
//                                    url.onClick(fragmentView);
//                                }
//                            }
//                        }
                    }

                    @Override
                    public void needOpenWebView(String url, String title, String description, String originalUrl, int w, int h) {
//                        BottomSheet.Builder builder = new BottomSheet.Builder(mContext);
//                        builder.setCustomView(new WebFrameLayout(mContext, builder.create(), title, description, originalUrl, url, w, h));
//                        builder.setUseFullWidth(true);
//                        showDialog(builder.create());
                    }

                    @Override
                    public void didPressedReplyMessage(ChatMessageCell cell, int id) {
//                        MessageObject messageObject = cell.getMessageObject();
//                        scrollToMessageId(id, messageObject.getId(), true, messageObject.getDialogId() == mergeDialogId ? 1 : 0);
                        Toast.makeText(parent.getContext(), "Action", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void didPressedViaBot(ChatMessageCell cell, String username) {
//                        if (bottomOverlayChat != null && bottomOverlayChat.getVisibility() == View.VISIBLE || bottomOverlay != null && bottomOverlay.getVisibility() == View.VISIBLE) {
//                            return;
//                        }
//                        if (chatActivityEnterView != null && username != null && username.length() > 0) {
//                            chatActivityEnterView.setFieldText("@" + username + " ");
//                            chatActivityEnterView.openKeyboard();
//                        }
                        Toast.makeText(parent.getContext(), "Action", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void didPressedImage(ChatMessageCell cell) {
                        MessageObject message = cell.getMessageObject();
                        if (message.isSendError()) {
//                            createMenu(cell, false);
                            return;
                        } else if (message.isSending()) {
                            return;
                        }
                        if (message.type == 13) {
//                            showDialog(new StickersAlert(getParentActivity(), message.getInputStickerSet(), null, bottomOverlayChat.getVisibility() != View.VISIBLE ? chatActivityEnterView : null));
                        } else if (Build.VERSION.SDK_INT >= 16 && message.isVideo() || message.type == 1 || message.type == 0 && !message.isWebpageDocument() || message.isGif()) {
                            PhotoViewer.getInstance().setParentActivity(ActivityTrends.this);
                            long dialog_id = 0;
                            long mergeDialogId = 0;
                            PhotoViewer.getInstance().openPhoto(message, message.type != 0 ? dialog_id : 0, message.type != 0 ? mergeDialogId : 0, ActivityTrends.this);
                        } else if (message.type == 3) {
//                            sendSecretMessageRead(message);
//                            try {
//                                File f = null;
//                                if (message.messageOwner.attachPath != null && message.messageOwner.attachPath.length() != 0) {
//                                    f = new File(message.messageOwner.attachPath);
//                                }
//                                if (f == null || !f.exists()) {
//                                    f = FileLoader.getPathToMessage(message.messageOwner);
//                                }
//                                Intent intent = new Intent(Intent.ACTION_VIEW);
//                                intent.setDataAndType(Uri.fromFile(f), "video/mp4");
//                                getParentActivity().startActivityForResult(intent, 500);
//                            } catch (Exception e) {
//                                alertUserOpenError(message);
//                            }
                        } else if (message.type == 4) {
//                            if (!AndroidUtilities.isGoogleMapsInstalled(ChatActivity.this)) {
//                                return;
//                            }
//                            NearbyActivity fragment = new NearbyActivity();
//                            fragment.setMessageObject(message);
//                            presentFragment(fragment);
                        } else if (message.type == 9 || message.type == 0) {
//                            try {
//                                AndroidUtilities.openForView(message, getParentActivity());
//                            } catch (Exception e) {
//                                alertUserOpenError(message);
//                            }
                        }
                        Toast.makeText(parent.getContext(), "Action", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder: position" + position);
            MessageObject message = messages.get(messages.size() - (position - messagesStartRow) - 1);
            View view = holder.itemView;
            boolean selected = false;
            boolean disableSelection = false;
            view.setBackgroundColor(0);
            if (view instanceof ChatMessageCell) {
                ChatMessageCell messageCell = (ChatMessageCell) view;
                messageCell.isChat = currentChat != null;
                messageCell.setMessageObject(message);
                messageCell.setCheckPressed(!disableSelection, disableSelection && selected);
                if (view instanceof ChatMessageCell && MediaController.getInstance().canDownloadMedia(MediaController.AUTODOWNLOAD_MASK_AUDIO)) {
                    ((ChatMessageCell) view).downloadAudioIfNeed();
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            int res;
            res = messages.get(messages.size() - (position - messagesStartRow) - 1).contentType;
            return res;
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ChatMessageCell) {
                final ChatMessageCell messageCell = (ChatMessageCell) holder.itemView;
                messageCell.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        messageCell.getViewTreeObserver().removeOnPreDrawListener(this);

                        int height = chatListView.getMeasuredHeight();
                        int top = messageCell.getTop();
                        int bottom = messageCell.getBottom();
                        int viewTop = top >= 0 ? 0 : -top;
                        int viewBottom = messageCell.getMeasuredHeight();
                        if (viewBottom > height) {
                            viewBottom = viewTop + height;
                        }
                        messageCell.setVisiblePart(viewTop, viewBottom - viewTop);

                        return true;
                    }
                });
            }
        }

        public void updateRowWithMessageObject(MessageObject messageObject) {
            int index = messages.indexOf(messageObject);
            if (index == -1) {
                return;
            }
            notifyItemChanged(messagesStartRow + messages.size() - index - 1);
        }

        @Override
        public void notifyDataSetChanged() {
            updateRows();
            try {
                super.notifyDataSetChanged();
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }
        }

        @Override
        public void notifyItemChanged(int position) {
            updateRows();
            try {
                super.notifyItemChanged(position);
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }
        }

        @Override
        public void notifyItemRangeChanged(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeChanged(positionStart, itemCount);
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }
        }

        @Override
        public void notifyItemInserted(int position) {
            updateRows();
            try {
                super.notifyItemInserted(position);
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }
        }

        @Override
        public void notifyItemMoved(int fromPosition, int toPosition) {
            updateRows();
            try {
                super.notifyItemMoved(fromPosition, toPosition);
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }
        }

        @Override
        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeInserted(positionStart, itemCount);
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }
        }

        @Override
        public void notifyItemRemoved(int position) {
            updateRows();
            try {
                super.notifyItemRemoved(position);
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }
        }

        @Override
        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeRemoved(positionStart, itemCount);
            } catch (Exception e) {
                FileLog.e("tmessages", e);
            }
        }

    }
}
