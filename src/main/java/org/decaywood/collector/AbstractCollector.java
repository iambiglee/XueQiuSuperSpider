package org.decaywood.collector;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.decaywood.timeWaitingStrategy.DefaultTimeWaitingStrategy;
import org.decaywood.timeWaitingStrategy.TimeWaitingStrategy;
import org.decaywood.utils.HttpRequestHelper;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * @author: decaywood
 * @date: 2015/11/23 13:51
 */
public abstract class AbstractCollector<T> implements Supplier<T> {

    public abstract T collectLogic() throws Exception;

    private TimeWaitingStrategy strategy;
    protected ObjectMapper mapper;

    public AbstractCollector(TimeWaitingStrategy strategy) {
        this.strategy = strategy == null ? new DefaultTimeWaitingStrategy<>() : strategy;
        this.mapper = new ObjectMapper();
    }

    @Override
    public T get() {

        this.strategy = this.strategy == null ? new DefaultTimeWaitingStrategy<>() : strategy;

        T res = null;

        try {
            int loopTime = 1;
            while (true) {
                try {
                    res = collectLogic();
                    break;
                } catch (Exception e) {
                    if(!(e instanceof IOException)) throw e;
                    HttpRequestHelper.updateCookie();
                    this.strategy.waiting(loopTime++);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;

    }
}
