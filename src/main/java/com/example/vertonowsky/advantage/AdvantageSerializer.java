package com.example.vertonowsky.advantage;

public class AdvantageSerializer {

    public static AdvantageDto serialize(Advantage advantage) {
        AdvantageDto dto = new AdvantageDto();
        dto.setAdvantageType(advantage.getAdvantageType());
        dto.setId(advantage.getId());
        dto.setIndex(advantage.getIndex());
        dto.setTitle(advantage.getTitle());
        dto.setPremium(advantage.isPremium());
        return dto;
    }

}
