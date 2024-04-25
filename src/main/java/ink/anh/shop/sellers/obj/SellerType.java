package ink.anh.shop.sellers.obj;

public enum SellerType {
    ENTITY(1, "Entity"), // Сутність
    BUTTON(2, "Button"), // Кнопка
    SIGN(3, "Sign"), // Табличка
    LEVER(4, "Lever"), // Ричаг
    DOOR(5, "Door"), // Двері,
    VILLAGER(6, "Villager"), // Сільський житель
    WANDERING_TRADER(7, "Wandering Trader");

    private final int id;
    private final String name;

    SellerType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
