package com.tartagliaeg.grmjava.datasources;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "github_repo")
public class GithubRepo {
  @PrimaryKey
  private final long id;

  @ColumnInfo(name = "name")
  private final String name;

  @ColumnInfo(name = "owner_name")
  private final String ownerName;

  @ColumnInfo(name = "url")
  @SerializedName("html_url")
  private final String url;

  @Ignore
  @SerializedName("owner")
  private final Owner owner;


  public GithubRepo(long id, String name, String url, Owner owner) {
    this.id = id;
    this.name = name;
    this.url = url;
    this.owner = owner;
    this.ownerName = owner.name;
  }

  public GithubRepo(long id, String name, String ownerName, String url) {
    this.id = id;
    this.name = name;
    this.ownerName = ownerName;
    this.owner = new Owner(ownerName);
    this.url = url;
  }

  public long getId() {
    return id;
  }

  public String getOwnerName() {
    return ownerName != null
      ? ownerName
      : owner.name;
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }


  public static class Owner {
    @SerializedName("login")
    public final String name;

    public Owner(String name) {
      this.name = name;
    }
  }
}
