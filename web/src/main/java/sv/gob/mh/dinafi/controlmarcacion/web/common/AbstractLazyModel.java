package sv.gob.mh.dinafi.controlmarcacion.web.common;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.apache.commons.beanutils.PropertyUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import sv.gob.mh.common.querydsl.AbstractService;
import sv.gob.mh.common.querydsl.CallBack;
import sv.gob.mh.common.querydsl.Filter;
import sv.gob.mh.common.querydsl.Page;
import sv.gob.mh.common.querydsl.Sort;


@Getter
@Setter
@Log
public class AbstractLazyModel<T, ID extends Serializable>
        extends LazyDataModel<T> {

    private static final long serialVersionUID = -5721429934812556873L;

    protected final AbstractService<T, ID> service;
    protected boolean isAny = true;
    protected CallBack<List<T>> afterLoad;
    protected Set<Filter> customFilters;
    protected Set<Sort> sortFirst;
    protected Set<Sort> sortLast;

    public AbstractLazyModel(AbstractService<T, ID> service) {
        this.service = service;
    }

    @Override
    public T getRowData(String rowKey) {
        T ret = null;
        if (service != null) {
            ID recid = null;
            Class idclass = service.getIdClazz();
            if (idclass.equals(String.class)) {
                recid = (ID) rowKey;
            } else if (idclass.equals(Long.class)) {
                recid = (ID) new Long(rowKey);
            } else if (idclass.equals(Integer.class)) {
                recid = (ID) new Integer(rowKey);
            }
            if (recid != null) {
                ret = service.findById(recid);
            }
        }
        return ret;
    }

    @Override
    public Object getRowKey(T object) {
        return object != null ? getProperty(object, "id") : null;
    }

    @Override
    public List<T> load(int first, int pageSize, String sortField,
            SortOrder sortOrder, Map<String, Object> filters) {

        Set<Sort> sortSet = null;
        if (sortField != null && !"".equals(sortField.trim())
                && sortOrder != null) {
            sortSet = new LinkedHashSet<>();
            String[] arrsorts = sortField.split("\\,");
            if (arrsorts != null && arrsorts.length > 0) {
                for (String sort : arrsorts) {
                    Sort osort = new Sort(sort.trim(),
                            SortOrder.ASCENDING == sortOrder
                                    ? Sort.SortDirection.ASCENDING
                                    : Sort.SortDirection.DESCENDING, null);
                    sortSet.add(osort);
                }
            }
        }
        Page<T> page = page(first, pageSize, filters, sortSet);
        setRowCount((int) page.getTotal());
        if (afterLoad != null) {
            afterLoad.setItem((List<T>) page.getContent());
            try {
                afterLoad.call();
            } catch (Exception ex) {
                afterLoadException(ex);
            }
        }
        return (List<T>) page.getContent();
    }

    protected void afterLoadException(Exception ex) {
        log.log(Level.SEVERE, null, ex);
    }

    @Override
    public List<T> load(int first, int pageSize, List<SortMeta> multiSortMeta,
            Map<String, Object> filters) {

        Set<Sort> sortSet = null;
        if (multiSortMeta != null && !multiSortMeta.isEmpty()) {
            sortSet = new LinkedHashSet<>();
            for (SortMeta sortMeta : multiSortMeta) {
                sortSet.add(
                        new Sort(sortMeta.getSortField(),
                                SortOrder.ASCENDING == sortMeta.getSortOrder()
                                ? Sort.SortDirection.ASCENDING
                                : Sort.SortDirection.DESCENDING, null)
                );
            }
        }
        Page<T> page = page(first, pageSize, filters, sortSet);
        setRowCount((int) page.getTotal());
        return (List<T>) page.getContent();
    }

    public Page<T> page(int first, int pageSize, Map<String, Object> filters,
            Set<Sort> sortSet) {

        Set<Filter> filterSet = new LinkedHashSet<>();
        if (filters != null && !filters.isEmpty()) {
            for (Map.Entry<String, Object> filter : filters.entrySet()) {
                filterSet.add(new Filter(
                        filter.getKey(), // attribute
                        filter.getValue(), // value 
                        null, // value2 
                        Filter.FilterOperator.CONTAINS,
                        !isAny()));
            }
        }
        if (customFilters != null) {
            filterSet.addAll(customFilters);
        }
        if (sortSet == null) {
            sortSet = new LinkedHashSet<>();
        }
        if (sortFirst != null) {
            sortSet.addAll(sortFirst);
        }
        if (sortLast != null) {
            sortSet.addAll(sortLast);
        }
        return service.findAll(first, pageSize, filterSet, sortSet);
    }

    public static Object getProperty(Object object, String sproperty) {
        Object ret = null;
        try {
            ret = PropertyUtils.getSimpleProperty(object, sproperty);
        } catch (IllegalAccessException | NoSuchMethodException
                | InvocationTargetException ex) {
            // does nothing
        }
        return ret;
    }
}
