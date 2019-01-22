package com.esprit.pim.breathlyzerv1.Entity;

/**
 * Created by zaineb on 17/01/16.
 */
public class Contact {
    public Integer id;
   public String name;
   public String number;


    public Contact(String name, String number) {
        this.name = name;
        this.number = number;
    }
public Contact(){

}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
