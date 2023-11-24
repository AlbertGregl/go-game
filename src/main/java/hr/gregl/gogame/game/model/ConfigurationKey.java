package hr.gregl.gogame.game.model;

public enum ConfigurationKey {

    HOST("server.host"),
    SERVER_PORT("server.port"),
    RMI_PORT("rmi.port");

    private final String keyName;

    ConfigurationKey(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyName() {
        return keyName;
    }

}
