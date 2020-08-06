package com.alphawallet.token.entity;

/**
 * Class for EthereumMessages to be sigden.
 * Weiwu, Aug 2020
*/
public class EthereumMessage {

    public final String value;
    public final String message;
    public final String displayOrigin;
    public final long leafPosition;

    public EthereumMessage(String message, String displayOrigin, long leafPosition) {
        this.value = message;
        this.message = message;
        this.displayOrigin = displayOrigin;
        this.leafPosition = leafPosition;
    }

}