package xyz.cofe.gui.swing.bind;

import org.junit.Test;
import xyz.cofe.gui.swing.Binder;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class BinderTest {
    public static class Bean1 {
        //region property change support
        private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            pcs.addPropertyChangeListener(listener);
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            pcs.removePropertyChangeListener(listener);
        }

        public PropertyChangeListener[] getPropertyChangeListeners() {
            return pcs.getPropertyChangeListeners();
        }

        public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
            pcs.addPropertyChangeListener(propertyName, listener);
        }

        public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
            pcs.removePropertyChangeListener(propertyName, listener);
        }

        public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
            return pcs.getPropertyChangeListeners(propertyName);
        }

        public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
            pcs.firePropertyChange(propertyName, oldValue, newValue);
        }

        public void firePropertyChange(String propertyName, int oldValue, int newValue) {
            pcs.firePropertyChange(propertyName, oldValue, newValue);
        }

        public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
            pcs.firePropertyChange(propertyName, oldValue, newValue);
        }

        public void firePropertyChange(PropertyChangeEvent event) {
            pcs.firePropertyChange(event);
        }

        public void fireIndexedPropertyChange(String propertyName, int index, Object oldValue, Object newValue) {
            pcs.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
        }

        public void fireIndexedPropertyChange(String propertyName, int index, int oldValue, int newValue) {
            pcs.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
        }

        public void fireIndexedPropertyChange(String propertyName, int index, boolean oldValue, boolean newValue) {
            pcs.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
        }

        public boolean hasListeners(String propertyName) {
            return pcs.hasListeners(propertyName);
        }
        //endregion

        private String str;
        public String getStr() { return str; }
        public void setStr(String str) {
            Object old = this.str;
            this.str = str;
            firePropertyChange("str", old, this.str);
        }
    }

    @Test
    public void test01(){
        Bean1 b1 = new Bean1();
        b1.setStr("a");

        Binder.bean(b1).property("str",String.class).bind(System.out::println);
        b1.setStr("b");
    }
}
