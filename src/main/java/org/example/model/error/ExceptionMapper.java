package org.example.model.error;

import lombok.NonNull;

public class ExceptionMapper {

    public void handle(@NonNull Exception e) {
        if (e instanceof UserError || e instanceof SystemError) {
            CustomError.handle((CustomError) e);
        } else {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

}
