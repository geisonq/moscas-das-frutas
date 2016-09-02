package com.geisonquevedo.moscadasfrutas.Database;

/**
 * Created by GeisonQuevedo on 25/04/2016.
 */
public class Coleta {

    private int _id;
    private int _id_armadilha;
    private String _data;
    private int _tc;
    private int _ta;

    public Coleta(){
    }

    public Coleta( int id_armadilha, String data, int ta, int tc){
        this._id_armadilha = id_armadilha;
        this._data = data;
        this._ta = ta;
        this._tc = tc;
    }

    public Coleta(int id, int id_armadilha, String data, int ta, int tc){
        this._id = id;
        this._id_armadilha = id_armadilha;
        this._data = data;
        this._ta = ta;
        this._tc = tc;
    }

    public int getID(){
        return this._id;
    }

    public void setID(int id){
        this._id = id;
    }

    public int getIdArmadilha(){
        return this._id_armadilha;
    }

    public void setIdArmadilha(int idArmadilha){
        this._id_armadilha = idArmadilha;
    }

    public String getData(){
        return this._data;
    }

    public void setData(String data){
        this._data = "2016-04-20 00:00:00.000";
    }

    public int getTa(){
        return this._ta;
    }

    public void setTa(int total){
        this._ta = total;
    }

    public int getTc(){
        return this._tc;
    }

    public void setTc(int total){
        this._tc = total;
    }
}
