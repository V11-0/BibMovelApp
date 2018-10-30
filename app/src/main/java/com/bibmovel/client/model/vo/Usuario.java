package com.bibmovel.client.model.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Usuario implements Parcelable {

	@SerializedName("nome") private String nome;
	@SerializedName("email") private String email;
	@SerializedName("senha") private String senha;
	@SerializedName("login") private String login;

	public Usuario() {}

	public Usuario(String login, String nome, String email, String senha) {
		this.login = login;
		this.nome = nome;
		this.email = email;
		this.senha = senha;
	}

    public Usuario(String login, String senha) {
        this.login = login;
        this.senha = senha;
    }

	public Usuario(String login, String nome, String email) {
		this.login = login;
		this.nome = nome;
		this.email = email;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		String[] data = {nome, email, senha, login};
		dest.writeStringArray(data);
	}

	public static final Parcelable.Creator<Usuario> CREATOR = new Creator<Usuario>() {
		@Override
		public Usuario createFromParcel(Parcel source) {
			return new Usuario(source.readString(), source.readString(), source.readString(), source.readString());
		}

		@Override
		public Usuario[] newArray(int size) {
			return new Usuario[0];
		}
	};
}
