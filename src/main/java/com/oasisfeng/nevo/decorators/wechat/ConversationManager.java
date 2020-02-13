package com.oasisfeng.nevo.decorators.wechat;

import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;
import java.util.regex.Pattern;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;

import static java.util.Objects.requireNonNull;

/**
 * Manage all conversations.
 *
 * Created by Oasis on 2019-4-11.
 */
class ConversationManager {

	private static final Person SENDER_PLACEHOLDER = new Person.Builder().setName(" ").build();	// Cannot be empty string, or it will be treated as null.

	static class Conversation {

		static final int TYPE_UNKNOWN = 0;
		static final int TYPE_DIRECT_MESSAGE = 1;
		static final int TYPE_GROUP_CHAT = 2;
		static final int TYPE_BOT_MESSAGE = 3;
		static final int TYPE_DM_RECALL = 4;
		static final int TYPE_GC_RECALL = 5;
		@IntDef({ TYPE_UNKNOWN, TYPE_DIRECT_MESSAGE, TYPE_GROUP_CHAT, TYPE_BOT_MESSAGE, TYPE_DM_RECALL, TYPE_GC_RECALL }) @Retention(RetentionPolicy.SOURCE) @interface ConversationType {}

		private static final String SCHEME_ORIGINAL_NAME = "ON:";
		private static final Pattern pattern = Pattern.compile("^[a-zA-Z0-9\\u4e00-\\u9fa5]");

		final int id;
		@Nullable String key;
		int count;
		CharSequence title;
		CharSequence summary;
		CharSequence ticker;
		long timestamp;
		IconCompat icon;
		private @Nullable Person.Builder sender;

		int getType() { return mType; }

		/** @return previous type */
		int setType(final int type) {
			if (type == mType) return type;
			final int previous_type = mType;
			mType = type;
			sender = type == TYPE_UNKNOWN || type == TYPE_GROUP_CHAT ? null	: sender().setKey(key).setBot(type == TYPE_BOT_MESSAGE);	// Always set key as it may change
			if (type != TYPE_GROUP_CHAT) mParticipants.clear();
			return previous_type;
		}

		@NonNull Person.Builder sender() {
			return sender != null ? sender : new Person.Builder() { @NonNull @Override public Person build() {
				switch (mType) {
				case TYPE_GROUP_CHAT:
				case TYPE_GC_RECALL:
					return SENDER_PLACEHOLDER;
				case TYPE_BOT_MESSAGE:
					setBot(true);	// Fall-through
				case TYPE_UNKNOWN:
				case TYPE_DIRECT_MESSAGE:
				case TYPE_DM_RECALL:
					setIcon(icon).setName(title != null ? title : " ");		// Cannot be empty string, or it will be treated as null.
					break;
				}
				Person r = super.build();
				// Log.d(TAG, "Person " + r.getIcon() + " " + r.getName());
				return r;
			}};
		}

		static boolean isGroupChat(int mType) { return mType == TYPE_GROUP_CHAT || mType == TYPE_GC_RECALL; }
		boolean isGroupChat() { return isGroupChat(mType); }
		static boolean isRecall(int mType) { return mType == TYPE_DM_RECALL || mType == TYPE_GC_RECALL; }
		boolean isRecall() { return isRecall(mType); }

		Person getGroupParticipant(final String key, final String name) {
			if (! isGroupChat()) throw new IllegalStateException("Not group chat");
			Person.Builder builder = null;
			Person participant = mParticipants.get(key);
			if (participant == null) builder = new Person.Builder().setKey(key);
			else if (! TextUtils.equals(name, requireNonNull(participant.getUri()).substring(SCHEME_ORIGINAL_NAME.length())))	// Original name is changed
				builder = participant.toBuilder();
			if (builder != null) {
				final CharSequence n = EmojiTranslator.translate(name);
				builder.setUri(SCHEME_ORIGINAL_NAME + name).setName(n);
				// if (!pattern.matcher(n).find()) { TODO
				// 	builder.setIcon(defaultIcon);
				// }
				mParticipants.put(key, participant = builder.build());
			}
			return participant;
		}

		static CharSequence getOriginalName(final Person person) {
			final String uri = person.getUri();
			return uri != null && uri.startsWith(SCHEME_ORIGINAL_NAME) ? uri.substring(SCHEME_ORIGINAL_NAME.length()): person.getName();
		}

		Conversation(final int id) { this.id = id; }

		@ConversationType private int mType;
		private final Map<String, Person> mParticipants = new ArrayMap<>();
	}

	Conversation getConversation(final int id) {
		Conversation conversation = mConversations.get(id);
		if (conversation == null) mConversations.put(id, conversation = new Conversation(id));
		return conversation;
	}

	private final SparseArray<Conversation> mConversations = new SparseArray<>();
	private static final String TAG = WeChatDecorator.TAG;
}
