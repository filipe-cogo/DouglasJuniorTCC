/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.cm.JGitMinerWeb.managedBean;

import br.edu.utfpr.cm.JGitMinerWeb.converter.ClassConverter;
import br.edu.utfpr.cm.JGitMinerWeb.dao.GenericDao;
import br.edu.utfpr.cm.JGitMinerWeb.pojo.matriz.EntityMatriz;
import br.edu.utfpr.cm.JGitMinerWeb.pojo.miner.EntityRepository;
import br.edu.utfpr.cm.JGitMinerWeb.services.matriz.MatrizServices;
import br.edu.utfpr.cm.JGitMinerWeb.util.JsfUtil;
import br.edu.utfpr.cm.JGitMinerWeb.util.OutLog;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author douglas
 */
@ManagedBean(name = "gitMatrizBean")
@SessionScoped
public class GitMatrizBean implements Serializable {


    /*
     * 
     */
    @EJB
    private GenericDao dao;
    private OutLog out;
    private EntityRepository repository;
    private String repositoryId;
    private Class serviceClass;
    private Map params;
    private String message;
    private Thread process;
    private Integer progress;
    private boolean initialized;
    private boolean fail;
    private boolean canceled;

    /**
     * Creates a new instance of GitNet
     */
    public GitMatrizBean() {
        out = new OutLog();
        params = new HashMap();
    }

    public boolean isFail() {
        return fail;
    }

    public void setFail(boolean fail) {
        this.fail = fail;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    public Class getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(Class serviceClass) {
        this.serviceClass = serviceClass;
    }

    public Map getParamValue() {
        return params;
    }

    public GenericDao getDao() {
        return dao;
    }

    public void setDao(GenericDao dao) {
        this.dao = dao;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public String getLog() {
        return out.getSingleLog();
    }

    public Integer getProgress() {
        if (fail) {
            progress = new Integer(100);
        } else if (progress == null) {
            progress = new Integer(0);
        } else if (progress > 100) {
            progress = new Integer(100);
        }
        System.out.println("progress: " + progress);
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public void start() {
        final EntityMatriz entityMatriz = new EntityMatriz();
        dao.insert(entityMatriz);
        out.resetLog();
        initialized = false;
        canceled = false;
        fail = false;
        progress = new Integer(0);

        repository = dao.findByID(repositoryId, EntityRepository.class);

        out.printLog("Geração da rede iniciada!");
        out.printLog("");
        out.printLog("Params: " + params);
        out.printLog("Class Service: " + serviceClass);
        out.printLog("Repository: " + repository);
        out.printLog("");

        entityMatriz.setLog(out.getLog().toString());
        entityMatriz.setClassServicesName(serviceClass.getName());
        dao.edit(entityMatriz);

        if (repository == null || serviceClass == null) {
            message = "Erro: Escolha o repositorio e o service desejado.";
            out.printLog(message);
            progress = new Integer(0);
            initialized = false;
            fail = true;
            entityMatriz.setLog(out.getLog().toString());
            dao.edit(entityMatriz);
        } else {
            initialized = true;
            progress = new Integer(10);
            entityMatriz.setRepository(repository);

            final MatrizServices netServices = createMatrizServiceInstance();

            process = new Thread(netServices) {
                @Override
                public void run() {
                    try {
                        out.setCurrentProcess("Iniciando consulta ao banco de dados.");
                        super.run();
                        out.printLog(netServices.getRecords().size() + " Registros encontrados na consulta!");
                        progress = new Integer(50);
                        out.printLog("");
                        out.setCurrentProcess("Iniciando salvamento dos dados gerados.");
                        entityMatriz.setRecords(netServices.getRecords());
                        entityMatriz.setComplete(true);
                        dao.edit(entityMatriz);
                        out.printLog("Salvamento dos dados concluído!");
                        message = "Geração da matriz concluída.";
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        message = "Geração da rede abortada: " + ex.toString();
                        fail = true;
                    }
                    System.gc();
                    out.printLog("");
                    out.setCurrentProcess(message);
                    progress = new Integer(100);
                    initialized = false;
                    entityMatriz.setLog(out.getLog().toString());
                    entityMatriz.setStoped(new Date());
                    dao.edit(entityMatriz);
                    params.clear();
                }
            };
            process.start();
        }
    }

    public void cancel() {
        if (initialized) {
            out.printLog("Pedido de cancelamento enviado.\n");
            canceled = true;
            try {
                process.interrupt();
            } catch (Exception ex) {
            }
        }
    }

    public void onComplete() {
        out.printLog("onComplete" + '\n');
        initialized = false;
        progress = new Integer(0);
        if (fail) {
            JsfUtil.addErrorMessage(message);
        } else {
            JsfUtil.addSuccessMessage(message);
        }
    }

    public List<Class> getServicesClasses() {
        List<Class> cls = null;
        try {
            cls = JsfUtil.getClasses("br.edu.utfpr.cm.JGitMinerWeb.services.matriz", Arrays.asList("MatrizServices"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cls;
    }

    private MatrizServices createMatrizServiceInstance() {
        try {
            return (MatrizServices) serviceClass.getConstructor(GenericDao.class, EntityRepository.class, Map.class).newInstance(dao, repository, params);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public ClassConverter getConverterClass() {
        return new ClassConverter();
    }
}
