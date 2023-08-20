package com.example.vertonowsky.avatar;

public class AvatarSerializer {

    public static AvatarDto serialize(Avatar avatar) {
        if (avatar == null) return new AvatarDto();

        AvatarDto dto = new AvatarDto();
        dto.setId(avatar.getId());
        dto.setName(avatar.getName());
        dto.setUrl(avatar.getUrl());
        return dto;
    }

}
