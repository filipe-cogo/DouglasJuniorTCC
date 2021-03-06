/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.JGitMinerWeb.services.matriz;

import br.edu.utfpr.cm.JGitMinerWeb.dao.GenericDao;
import br.edu.utfpr.cm.JGitMinerWeb.pojo.matriz.EntityMatrizRecord;
import br.edu.utfpr.cm.JGitMinerWeb.pojo.miner.EntityRepository;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author douglas
 */
public abstract class MatrizServices implements Runnable, Serializable {

    protected GenericDao dao;
    protected EntityRepository repository;
    private List<EntityMatrizRecord> records;
    protected Map params;

    public MatrizServices(GenericDao dao) {
        this.dao = dao;
    }

    public MatrizServices(GenericDao dao, EntityRepository repository, Map params) {
        this.dao = dao;
        this.repository = repository;
        this.params = params;
    }

    public EntityRepository getRepository() {
        return repository;
    }

    public List<EntityMatrizRecord> getRecords() {
        return records;
    }

    public void setRecords(List<EntityMatrizRecord> records) {
        this.records = records;
    }

    @Override
    public abstract void run();

    public abstract String convertToCSV(List<EntityMatrizRecord> records);

    public static MatrizServices createInstance(GenericDao dao, String className) {
        try {
            return (MatrizServices) Class.forName(className).getConstructor(GenericDao.class).newInstance(dao);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    protected Date getDateParam(Object key) {
        if (!params.containsKey(key)) {
            throw new IndexOutOfBoundsException(key + "");
        }
        Object obj = params.get(key);
        if (obj != null) {
            if (obj instanceof Date) {
                return (Date) obj;
            } else {
                throw new ClassCastException(key + "");
            }
        }
        return null;
    }
}