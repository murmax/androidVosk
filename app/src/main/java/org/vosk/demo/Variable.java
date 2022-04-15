package org.vosk.demo;

/*
 Описывает переменные, существующие перманентно. Покамест будут строковые только.
 */
public class Variable {
    static private  int maxId = 1;
    private final int id;
    public String name;
    public String descr;
    public String value;

    Variable(String name,String descr, String value, Integer id)
    {
        if (id ==null)
        {
            maxId++;
            this.id = maxId;
        } else
        {
            this.id = id;
            if(id>maxId) maxId = id;
        }
        this.name = name;
        this.descr = descr;
        this.value = value;
    }

    public int getId() {
        return id;
    }
}
