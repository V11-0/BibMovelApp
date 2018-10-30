package com.bibmovel.client.utils;

import java.net.MalformedURLException;

import jcifs.smb.SmbFile;

/**
 * Created by vinibrenobr11 on 18/09/2018 at 10:11
 */
public abstract class ConnectionFactory {

    public static SmbFile getSmbConnection() throws MalformedURLException {
        return new SmbFile("smb://192.168.0.1/Public/Books/");
    }
}