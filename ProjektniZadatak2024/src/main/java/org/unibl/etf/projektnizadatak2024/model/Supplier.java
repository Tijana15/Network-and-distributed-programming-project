package org.unibl.etf.projektnizadatak2024.model;

import java.io.Serializable;
import java.util.List;

public class Supplier implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;
    private List<Book> books;

    public Supplier(String name, List<Book> books) {
        this.name = name;
        this.books = books;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "name='" + name + '\'' +
                ", books=" + books +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Supplier supplier = (Supplier) o;
        return name.equals(((Supplier) o).name);
    }
}
