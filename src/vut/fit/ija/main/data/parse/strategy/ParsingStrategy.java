package vut.fit.ija.main.data.parse.strategy;

/**
 * Strategy interface for strategy design pattern
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 22-3-2020, xkarpi06
 */
public interface ParsingStrategy {
    public Object parseLine(String line);
}
