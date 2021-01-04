package com.example.shipbattle;

public class Lobby {
    private String id;
    private String idCreator;
    private String idOpponent;

    private Lobby() {}

    public Lobby(String id, String idCreator) {
        this.id = id;
        this.idCreator = idCreator;
    }

    public String getId() {
        return id;
    }

    public String getIdCreator() {
        return idCreator;
    }

    public String getIdOpponent() {
        return idOpponent;
    }

    public void setIdOpponent(String idOpponent) {
        this.idOpponent = idOpponent;
    }
}
