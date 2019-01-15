package com.implemica.CurrencyConverter;

import com.implemica.CurrencyConverter.controller.BotControllerTest;
import com.implemica.CurrencyConverter.controller.SecurityTest;
import com.implemica.CurrencyConverter.controller.WebControllerTest;
import com.implemica.CurrencyConverter.dao.impl.DialogDaoImplTest;
import com.implemica.CurrencyConverter.service.BotServiceTest;
import com.implemica.CurrencyConverter.service.ConverterTest;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectClasses({BotServiceTest.class, DialogDaoImplTest.class, SecurityTest.class,
        ConverterTest.class, WebControllerTest.class, BotControllerTest.class})
public class AllTests {
}

