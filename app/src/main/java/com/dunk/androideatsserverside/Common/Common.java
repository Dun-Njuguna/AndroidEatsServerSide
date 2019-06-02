package com.dunk.androideatsserverside.Common;


import com.dunk.androideatsserverside.model.User;

public class Common {
    public static User currentUser;
    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";

    public static final String convertCodeToStatus(String code){
        if (code.equals("0")){
            return "Placed";
        }
        else if (code.equals("1")){
            return "Shipping";
        }else
            return "Shipped";

    }

}
