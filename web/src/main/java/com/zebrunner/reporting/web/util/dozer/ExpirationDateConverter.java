package com.zebrunner.reporting.web.util.dozer;

import com.zebrunner.reporting.service.util.DateTimeUtil;
import org.dozer.DozerConverter;

import java.util.Date;
/**
 * ExpirationDateConverter - converts expiresIn seconds to expidationDate.
 * 
 * @author akhursevich
 */
public class ExpirationDateConverter extends DozerConverter<Integer, Date> {

    public ExpirationDateConverter() {
        super(Integer.class, Date.class);
    }

    @Override
    public Integer convertFrom(Date source, Integer destination) {
        return source != null ? ((Long) DateTimeUtil.toSecondsSinceDateToNow(source)).intValue() : null;
    }

    @Override
    public Date convertTo(Integer source, Date destination) {
        return source != null ? DateTimeUtil.toDateSinceNowPlusSeconds(source) : null;
    }
}
