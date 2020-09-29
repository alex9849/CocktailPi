package net.alex9849.cocktailmaker.model.util;

public interface Observable<T> {

    boolean addListener(Observer<T> observer);

    boolean removeListener(Observer<T> observer);

}
