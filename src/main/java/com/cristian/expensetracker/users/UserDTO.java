package com.cristian.expensetracker.users;

public record UserDTO(String displayName, String email, String timezone, String preferredCurrencyCode) {

        public static UserDTO from(UserEntity user) {
            return new UserDTO(
                    user.getDisplayName(),
                    user.getEmail(),
                    user.getTimezone(),
                    user.getPreferredCurrency().getCurrencyCode()
            );
        }
}

