package com.example.vertonowsky.user;

import com.example.vertonowsky.avatar.AvatarSerializer;
import com.example.vertonowsky.payment.PaymentHistorySerializer;
import org.hibernate.Hibernate;

import java.util.HashMap;
import java.util.Map;

public class UserSerializer {

    private static final Map<Task, SerializationTask> serializationTasks = initializeSerializationTasks();

    public enum Task {
        BASE, AVATAR, PAYMENTS;
    }

    private interface SerializationTask {
        void serialize(UserInfoDto userInfoDto, User user);
    }

    private static Map<Task, SerializationTask> initializeSerializationTasks() {
        Map<Task, SerializationTask> tasks = new HashMap<>();
        tasks.put(Task.BASE, UserSerializer::base);
        tasks.put(Task.AVATAR, UserSerializer::avatar);
        tasks.put(Task.PAYMENTS, UserSerializer::payments);
        return tasks;
    }

    private static void base(UserInfoDto userInfoDto, User user) {
        userInfoDto.setEmail(user.getEmail());
    }

    private static void avatar(UserInfoDto userInfoDto, User user) {
        userInfoDto.setAvatar(AvatarSerializer.serialize(user.getAvatar()));
    }

    private static void payments(UserInfoDto userInfoDto, User user) {
        if (Hibernate.isInitialized(user.getPaymentHistories()) && user.getPaymentHistories() != null)
            userInfoDto.setPaymentHistories(user.getPaymentHistories().stream().map(PaymentHistorySerializer::serialize).toList());
    }

    public static UserInfoDto serialize(User user, Task... tasks) {
        UserInfoDto dto = new UserInfoDto();

        for (Task task : tasks) {
            SerializationTask serializationTask = serializationTasks.get(task);
            if (serializationTask != null) {
                serializationTask.serialize(dto, user);
            }
        }

        return dto;
    }

}
