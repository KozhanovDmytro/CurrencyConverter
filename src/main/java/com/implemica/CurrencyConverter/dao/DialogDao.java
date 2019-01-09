package com.implemica.CurrencyConverter.dao;

import com.implemica.CurrencyConverter.model.Dialog;

import java.util.Date;
import java.util.List;

/**
 * Class for write {@link Dialog} into a storage
 *
 * @see Dialog
 * @see Date
 * @see List
 *
 * @author Dmytro K.
 * @version 25.12.2018 18:00
 */
public interface DialogDao {

   /**
    * Write into a storage
    *
    * @param dialog info
    */
   void write(Dialog dialog);

   /**
    * Gets all {@link Dialog}s from storage
    *
    * @return list
    */
   List<Dialog> getAll();

   /**
    * Gets desired {@link Dialog} by one day.
    *
    * @param date date, which for is needed to get information
    * @return instance
    */
   List<Dialog> getByDate(Date date);
}
