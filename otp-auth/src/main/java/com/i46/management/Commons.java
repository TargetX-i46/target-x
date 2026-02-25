package com.i46.management;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Commons {
    @Value("${app.dir.home}")
    public String HOME_DIRECTORY;

}
