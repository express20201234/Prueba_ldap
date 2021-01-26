package sv.gob.mh.dinafi.controlmarcacion.web.backend;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.persistence.PersistenceException;
import javax.servlet.http.Part;
import javax.transaction.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.apache.commons.beanutils.BeanMap;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;
import org.primefaces.shaded.commons.io.IOUtils;
import sv.gob.mh.common.querydsl.AbstractRepository;
import sv.gob.mh.common.querydsl.AbstractService;
import sv.gob.mh.common.querydsl.ViewStatus;
import sv.gob.mh.dinafi.controlmarcacion.web.common.LabeledItem;

@Getter
@Setter
@Log
public abstract class CrudBackend<T extends Serializable, ID extends Serializable> implements Serializable {

    private static final long serialVersionUID = 9149746672669981920L;
    public static final String FILE_SEPARATOR = File.separator;
    public static final String TAG_ERROR = "ERROR";
    public static final String TAG_WARN = "WARN";
    public static final String TAG_INFO = "INFO";

    protected transient T editableItem;
    private transient List<T> data;
    protected Class<T> clazz;
    protected Class<ID> idclazz;
    protected ViewStatus viewStatus = ViewStatus.NONE;
    protected transient Part file;

    public CrudBackend() {
        Type[] args = null;
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            args = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
        }

        if (args != null && args.length >= 2) {
            clazz = (Class) args[0];
            idclazz = (Class) args[1];
        }
    }

    @PostConstruct
    public void postConstruct() {
        beforePostConstruct();
        // more postConstruct initializations...
        //...
        init();
    }

    public void beforePostConstruct() {
        // to be implemented in children classes
    }

    public void init() {
        // to be implemented in children classes
    }

    public abstract AbstractService<T, ID> getService();

    public void resetStatus() {
        viewStatus = ViewStatus.NONE;
    }

    public void fillData() {
        setData(getService().findAll());
    }

    public List<T> getDataLively() {
        fillData();
        return getData();
    }

    public void fillDtoList() {
        List list = getDtoList();
        setDtoList(list);
    }

    public void setDtoList(List list) {
    }

    public List getDtoList() {
        return getService().findAllDto();
    }

    public void prepareNew() throws InstantiationException, IllegalAccessException {
        editableItem = (T) clazz.newInstance();
        viewStatus = ViewStatus.NEW;
        afterNew();
        afterSelect();
    }

    public void prepareView(T item) {
        editableItem = item;
        viewStatus = ViewStatus.VIEW;
        afterSelect();
    }

    public String openItem(T item) {
        if (item != null) {
            item.getClass();
        }
        return null;
    }

    public void afterSelect() {
        // to be implemented in children classes
    }

    public void afterNew() {
        // to be implemented in children classes
    }

    public void afterSave() {
        // to be implemented in children classes
    }

    public void afterSaveChanges() {
        // to be implemented in children classes
    }

    public void afterDelete() {
        // to be implemented in children classes
    }

    public void save() {
        if (editableItem != null) {
            try {
                beforeSave();
                getService().save(editableItem);
                editableItem = null;
                resetStatus();
                fillData();
                afterSave();
            } catch (Exception ex) {
                crudException(ex, "No fue posible Guardar.");
            }
        }
    }

    public void prepareEdit(T item) {
        editableItem = item;
        viewStatus = ViewStatus.EDIT;
        afterSelect();
    }

    public void saveChanges() {
        if (editableItem != null) {
            try {
                beforeSaveChanges();
                getService().save(editableItem);
                editableItem = null;
                resetStatus();
                fillData();
                afterSaveChanges();
            } catch (Exception ex) {
                crudException(ex, "No fue posible Actualizar.");
            }
        }
    }

    public void prepareDelete() {
        viewStatus = ViewStatus.REMOVE;
        afterSelect();
    }

    public void delete(T item) {
        try {
            beforeDelete(item);
            getService().delete(item);
            resetStatus();
            fillData();
            afterDelete();
        } catch (Exception ex) {
            crudException(ex, "No fue posible Borrar.");
        }
    }

    public void saveInserting() {
        if (editableItem != null) {
            try {
                beforeSaveInserting();
                getService().insert(editableItem);
                editableItem = null;
                resetStatus();
                fillData();
                afterSave();
            } catch (Exception ex) {
                crudException(ex, "No fue posible Guardar.");
            }
        }
    }

    public void saveChangesUpdating() {
        if (editableItem != null) {
            try {
                beforeSaveChangesUpdating();
                getService().update(editableItem);
                editableItem = null;
                resetStatus();
                fillData();
                afterSaveChanges();
            } catch (Exception ex) {
                crudException(ex, "No fue posible Actualizar.");
            }
        }
    }

    public void setFile(Part part) {
        this.file = part;
        boolean saved = saveFile(part);
        if (editableItem != null) {
            String fileName = part.getSubmittedFileName();
            BeanMap entityMap = new BeanMap(editableItem);
            log.log(Level.INFO, "Saving image into server image :{0} - result: {1}",
                    new Object[]{fileName, saved});
            entityMap.put(getImageAttribute(), "resources/img/" + fileName);
        }
    }

    public boolean saveFile(Part part) {
        boolean ret = false;
        InputStream is = null;
        byte[] bytes = null;
        try {
            is = part.getInputStream();
            bytes =  IOUtils.toByteArray(is);
        } catch (IOException ex) {
            // nothing to do
        }
        ret = afterSaveFile(bytes);
        return ret;
    }

    private static String getFilename(Part part) {
        String ret = null;
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                String filename = cd.substring(cd.indexOf('=') + 1).trim()
                        .replace("\"", "");
                ret = filename.substring(filename.lastIndexOf('/') + 1)
                        .substring(filename.lastIndexOf('\\') + 1); // MSIE fix.
                break;
            }
        }
        return ret;
    }

    public String getImageAttribute() {
        return "imagen";
    }

    public String getIdAttribute() {
        String ret = "id";
        AbstractService<T, ID> service = getService();
        if (service != null) {
            AbstractRepository<T, ID> repository = service.getRepository();
            if (repository != null) {
                ret = repository.getIdAttribute();
            }
        }
        return ret;
    }

    public String editMessage(String typeException, String message) {
        if (typeException != null) {
            log.log(Level.INFO, null, typeException);
        }
        return message;
    }

    public void crudException(Exception ex, String defaultMessage) {
        crudException(ex, defaultMessage, null);
    }

    public void crudException(Exception ex, String defaultMessage, String level) {
        LabeledItem result = manageException(ex, defaultMessage, false);
        String message = editMessage(result.getLabel(), result.getValue().toString());
        if (level == null) {
            Messages.create(TAG_ERROR).detail(message, (Object[]) null).error().add();
        } else if (TAG_ERROR.equalsIgnoreCase(level.trim())) {
            Messages.create(TAG_ERROR).detail(message, (Object[]) null).error().add();
        } else if (TAG_WARN.equalsIgnoreCase(level.trim())) {
            Messages.create("ADVERTENCIA").detail(message, (Object[]) null).warn().add();
        } else if (TAG_INFO.equalsIgnoreCase(level.trim())) {
            Messages.create(TAG_INFO).detail(message, (Object[]) null).add();
        }
        Faces.validationFailed();
    }

    public static LabeledItem<String> manageException(Exception ex, String defaultMessage) {
        return manageException(ex, defaultMessage, true);
    }

    public static LabeledItem<String> manageException(Exception ex, String defaultMessage, boolean addFacesErrors) {
        String error = defaultMessage;
        String nameException = ex.getClass().getSimpleName();
        if (ex.getCause() != null && ex.getCause() instanceof RollbackException) {
            RollbackException rbe = (RollbackException) ex.getCause();
            nameException = rbe.getClass().getSimpleName();
            if (rbe.getCause() != null) {
                if (rbe.getCause() instanceof PersistenceException) {
                    PersistenceException px = (PersistenceException) rbe.getCause();
                    nameException = px.getClass().getSimpleName();
                    if (px.getCause() != null) {
                        if (px.getCause() instanceof ConstraintViolationException) {
                            ConstraintViolationException cv = (ConstraintViolationException) px.getCause();
                            nameException = cv.getClass().getSimpleName();
                            if (cv.getCause() != null) {
                                if (cv.getCause() instanceof SQLIntegrityConstraintViolationException) {
                                    SQLIntegrityConstraintViolationException sqie = (SQLIntegrityConstraintViolationException) cv.getCause();
                                    nameException = sqie.getClass().getSimpleName();
                                    error = sqie.getMessage();

                                    String em = error;
                                    em = em.replaceFirst(".+\\(", "");
                                    em = em.replaceFirst("\\).+$", "");
                                    int beginIndex = em.indexOf('.');
                                    int endIndex = em.length();
                                    if (beginIndex >= 0 && (beginIndex + 1) <= endIndex) {
                                        em = em.substring(beginIndex + 1, endIndex);
                                    }
                                    em = em.trim();
                                    String message = Faces.getBundleString(em);
                                    if (message != null && !"".equals(message.trim())) {
                                        error = message.trim();
                                    }
                                } else if (cv.getCause() instanceof SQLException) {
                                    error = cv.getCause().getMessage();
                                } else {
                                    error = cv.toString();
                                }
                            } else if (cv.getConstraintViolations() != null && !cv.getConstraintViolations().isEmpty()) {
                                error = "";
                                for (ConstraintViolation<?> constviola : cv.getConstraintViolations()) {
                                    error += "<strong>" + constviola.getPropertyPath() + "</strong> " + constviola.getMessage() + "<br />";
                                }
                                error += "</ul>";
                            }
                        } else if (px.toString().contains("org.hibernate.exception.ConstraintViolationException")) {
                            Throwable cause1 = px.getCause();
                            if (cause1 != null) {
                                Throwable cause2 = cause1.getCause();
                                if (cause2 != null) {
                                    error = cause2.getLocalizedMessage();
                                } else {
                                    error = cause1.toString();
                                }
                            } else {
                                error = px.toString();
                            }
                        } else {
                            error = px.toString();
                        }
                    }
                } else {
                    error = rbe.toString();
                }
            }
        }

        if (addFacesErrors) {
            Messages.create(TAG_ERROR).detail(error, (Object[]) null).error().add();
            Faces.validationFailed();
        }

        return new LabeledItem<>(nameException, error);
    }

    public void beforeSave() {
        // to be implemented in children classes
    }

    public void beforeSaveChanges() {
        // to be implemented in children classes
    }

    public void beforeDelete(T item) {
        // to be implemented in children classes
    }

    public void beforeSaveInserting() {
        // to be implemented in children classes
    }

    public void beforeSaveChangesUpdating() {
        // to be implemented in children classes
    }

    public boolean afterSaveFile(byte[] is) {
        // to be implemented in children classes
        return true;
    }
}
