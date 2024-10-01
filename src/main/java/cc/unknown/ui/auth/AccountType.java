package cc.unknown.ui.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AccountType {
    MICROSOFT("Microsoft");
	
	final String name;
	
    public static AccountType getByName(String name) {
        for (AccountType type : values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }

        return null;
    }
}
