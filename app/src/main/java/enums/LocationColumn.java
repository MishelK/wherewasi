package enums;

public enum LocationColumn {
    ID("id"),
    LATITUDE("latitude"),
    LONGITUDE("longitude"),
    PROVIDER("provider"),
    UPDATED_TIME("updated_time"),
    ADDRESS_LINE("address_line"),
    ADMIN_AREA("admin_area"),
    COUNTRY_CODE("country_code"),
    FEATURE_NAME("feature_name"),
    SUB_AREA_NAME("sub_area_name");

    private String name;

    LocationColumn(String s) {
        name = s;
    }

    public String toString() {
        return name;
    }
}
