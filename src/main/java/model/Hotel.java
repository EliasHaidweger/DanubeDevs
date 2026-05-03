package model;

public record Hotel(
        int id,
        int noRooms,
        int noBeds,
        String category,
        String name,
        String owner,
        String contact,
        String address,
        String city,
        String cityCode,
        String phone,
        String tags
) {}
