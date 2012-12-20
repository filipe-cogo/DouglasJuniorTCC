package br.edu.utfpr.cm.JGitMinerWeb.managedBean;

import br.edu.utfpr.cm.JGitMinerWeb.dao.GenericDao;
import br.edu.utfpr.cm.JGitMinerWeb.pojo.*;
import br.edu.utfpr.cm.JGitMinerWeb.services.*;
import br.edu.utfpr.cm.JGitMinerWeb.util.JsfUtil;
import br.edu.utfpr.cm.JGitMinerWeb.util.out;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.eclipse.egit.github.core.*;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.IssueService;

@ManagedBean(name = "gitOthersBean")
@SessionScoped
public class GitOthersBean implements Serializable {

    @EJB
    private GenericDao dao;
    private EntityRepository repositoryToMiner;
    private boolean minerOpenIssues;
    private boolean minerClosedIssues;
    private boolean minerCommentsOfIssues;
    private boolean minerCollaborators;
    private boolean minerWatchers;
    private boolean minerOpenPullRequests;
    private boolean minerClosedPullRequests;
    private boolean minerForks;
    private boolean minerRepositoryCommits;
    private boolean minerCommentsOfRepositoryCommits;
    private boolean minerTeams;
    private boolean initialized;
    private Integer progress;
    private Integer subProgress;
    private String message;
    private boolean canceled;
    private Thread process;
    private boolean fail;

    public GitOthersBean() {
        initialized = false;
        canceled = false;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean isMinerCommentsOfIssues() {
        return minerCommentsOfIssues;
    }

    public void setMinerCommentsOfIssues(boolean minerCommentsOfIssues) {
        this.minerCommentsOfIssues = minerCommentsOfIssues;
    }

    public boolean isMinerClosedIssues() {
        return minerClosedIssues;
    }

    public void setMinerClosedIssues(boolean minerClosedIssues) {
        this.minerClosedIssues = minerClosedIssues;
    }

    public boolean isMinerOpenIssues() {
        return minerOpenIssues;
    }

    public void setMinerOpenIssues(boolean minerOpenIssues) {
        this.minerOpenIssues = minerOpenIssues;
    }

    public boolean isMinerCollaborators() {
        return minerCollaborators;
    }

    public void setMinerCollaborators(boolean minerCollaborators) {
        this.minerCollaborators = minerCollaborators;
    }

    public boolean isMinerClosedPullRequests() {
        return minerClosedPullRequests;
    }

    public void setMinerClosedPullRequests(boolean minerClosedPullRequests) {
        this.minerClosedPullRequests = minerClosedPullRequests;
    }

    public boolean isMinerOpenPullRequests() {
        return minerOpenPullRequests;
    }

    public void setMinerOpenPullRequests(boolean minerOpenPullRequests) {
        this.minerOpenPullRequests = minerOpenPullRequests;
    }

    public boolean isMinerForks() {
        return minerForks;
    }

    public void setMinerForks(boolean minerForks) {
        this.minerForks = minerForks;
    }

    public boolean isMinerCommentsOfRepositoryCommits() {
        return minerCommentsOfRepositoryCommits;
    }

    public void setMinerCommentsOfRepositoryCommits(boolean minerCommentsOfRepositoryCommits) {
        this.minerCommentsOfRepositoryCommits = minerCommentsOfRepositoryCommits;
    }

    public boolean isMinerRepositoryCommits() {
        return minerRepositoryCommits;
    }

    public void setMinerRepositoryCommits(boolean minerRepositoryCommits) {
        this.minerRepositoryCommits = minerRepositoryCommits;
    }

    public boolean isMinerTeams() {
        return minerTeams;
    }

    public void setMinerTeams(boolean minerTeams) {
        this.minerTeams = minerTeams;
    }

    public boolean isMinerWatchers() {
        return minerWatchers;
    }

    public void setMinerWatchers(boolean minerWatchers) {
        this.minerWatchers = minerWatchers;
    }

    public EntityRepository getRepositoryToMiner() {
        return repositoryToMiner;
    }

    public void setRepositoryToMiner(EntityRepository repositoryToMiner) {
        this.repositoryToMiner = repositoryToMiner;
    }

    public String getCurrentProcess() {
        return out.getCurrentProcess();
    }

    public String getLog() {
        return out.getLog();
    }

    public void start() {
        out.resetLog();
        initialized = false;
        canceled = false;
        progress = new Integer(0);
        subProgress = new Integer(0);
        final EntityMiner mineration = new EntityMiner();

        dao.insert(mineration);

        out.printLog("Repositorio: " + repositoryToMiner);
        out.printLog("minerOpenIssues: " + minerOpenIssues);
        out.printLog("minerClosedIssues: " + minerClosedIssues);
        out.printLog("minerComments: " + minerCommentsOfIssues);
        out.printLog("minerCollaborators: " + minerCollaborators);
        out.printLog("minerWatchers: " + minerWatchers);
        out.printLog("minerOpenPullRequests: " + minerOpenPullRequests);
        out.printLog("minerClosedPullRequests: " + minerClosedPullRequests);
        out.printLog("minerRepositoryCommits: " + minerRepositoryCommits);
        out.printLog("minerCommentsOfRepositoryCommits: " + minerCommentsOfRepositoryCommits);
        out.printLog("minerTeams: " + minerTeams);

        if (repositoryToMiner == null) {
            message = "Erro: Escolha o repositorio desejado.";
            out.printLog(message);
            progress = new Integer(0);
            subProgress = new Integer(0);
            initialized = false;
            fail = true;
            mineration.setMinerLog(out.getLog());
            dao.edit(mineration);
            return;
        }
        process = new Thread() {
            @Override
            public void run() {

                out.printLog("########### PROCESSO DE MINERAÇÃO INICIADO! ##############\n");

                try {
                    initialized = true;
                    Thread.sleep(5000);
                    Repository gitRepo = RepositoryServices.getGitRepository(repositoryToMiner.getOwner().getLogin(), repositoryToMiner.getName());
                    System.out.println("Repositorio escolhido: " + gitRepo);
                    progress = new Integer(10);
                    if (!canceled && (minerOpenIssues || minerClosedIssues)) {
                        subProgress = new Integer(0);
                        out.setCurrentProcess("Minerando issues...\n");
                        List<Issue> gitIssues = IssueServices.getGitIssuesFromRepository(gitRepo, minerOpenIssues, minerClosedIssues);
                        minerIssues(gitIssues, gitRepo);
                        mineration.setMinerLog(out.getLog());
                        dao.edit(mineration);
                    }
                    progress = new Integer(25);
                    if (!canceled && minerCollaborators) {
                        subProgress = new Integer(0);
                        out.setCurrentProcess("Minerando collaborators...\n");
                        List<User> collaborators = UserServices.getGitCollaboratorsFromRepository(gitRepo);
                        minerCollaborators(collaborators);
                        mineration.setMinerLog(out.getLog());
                        dao.edit(mineration);
                    }
                    progress = new Integer(50);
                    if (!canceled && minerWatchers) {
                        subProgress = new Integer(0);
                        out.setCurrentProcess("Minerando watchers...\n");
                        List<User> wacthers = UserServices.getGitWatchersFromRepository(gitRepo);
                        minerWatchers(wacthers);
                        mineration.setMinerLog(out.getLog());
                        dao.edit(mineration);
                    }
                    progress = new Integer(75);
                    if (!canceled && (minerClosedPullRequests || minerOpenPullRequests)) {
                        subProgress = new Integer(0);
                        out.setCurrentProcess("Minerando Pull Requests...\n");
                        List<PullRequest> gitPullRequests = PullRequestServices.getGitPullRequestsFromRepository(gitRepo, minerOpenPullRequests, minerClosedPullRequests);
                        minerPullRequests(gitPullRequests);
                        dao.edit(mineration);
                    }
                    progress = new Integer(80);
                    if (!canceled && minerForks) {
                        subProgress = new Integer(0);
                        out.setCurrentProcess("Minerando Forks...\n");
                        List<Repository> gitForks = RepositoryServices.getGitForksFromRepository(gitRepo);
                        minerForks(gitForks);
                        dao.edit(mineration);
                    }
                    if (!canceled && minerRepositoryCommits) {
                        subProgress = new Integer(0);
                        out.setCurrentProcess("Minerando RepositoryCommits...\n");
                        List<RepositoryCommit> gitRepoCommits = RepositoryCommitServices.getGitCommitsFromRepository(gitRepo);
                        minerRepositoryCommits(gitRepoCommits, gitRepo);
                        dao.edit(mineration);
                    }
                    if (!canceled && minerTeams) {
                        subProgress = new Integer(0);
                        out.setCurrentProcess("Minerando Teams...\n");
                        List<Team> gitTeams = TeamServices.getGitTeamsFromRepository(gitRepo);
                        minerTeams(gitTeams, gitRepo);
                        dao.edit(mineration);
                    }
                    if (canceled) {
                        out.printLog("Processo de mineração cancelado pelo usuário.\n");
                    }
                    mineration.setComplete(true);
                    message = "Mineração finalizada.";
                } catch (Exception ex) {
                    ex.printStackTrace();
                    message = "Mineração abortada: " + ex.toString();
                    mineration.setComplete(false);
                    fail = true;
                }
                System.gc();
                out.setCurrentProcess(message);
                progress = new Integer(100);
                initialized = false;
                mineration.setMinerStop(new Date());
                mineration.setMinerLog(out.getLog());
                dao.edit(mineration);
            }
        };
        process.start();
    }

    public void cancel() {
        if (initialized) {
            out.printLog("Pedido de cancelamento enviado.\n");
            canceled = true;
            process.interrupt();
        }
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

    public Integer getSubProgress() {
        if (fail) {
            subProgress = new Integer(100);
        } else if (subProgress == null) {
            subProgress = new Integer(0);
        } else if (subProgress > 100) {
            subProgress = new Integer(100);
        }
        return subProgress;
    }

    public void onComplete() {
        out.printLog("onComplete" + '\n');
        initialized = false;
        progress = new Integer(0);
        subProgress = new Integer(0);
        if (fail) {
            JsfUtil.addErrorMessage(message);
        } else {
            JsfUtil.addSuccessMessage(message);
        }
    }

    private void calculeSubProgress(double i, double size) {
        double subProg = (i / size) * 100;
        subProgress = new Integer((int) subProg);
    }

    public void teste() {
//        EntityIssue issue = new EntityIssue();
//        issue.setIdIssue(234234234);
//        issue.setUserIssue((EntityUser) dao.findByID(new Long(12), EntityIssue.class));
//        dao.insert(issue);
//        System.out.println("Gravou issue: " + issue);
        System.out.println("clicou");
    }

    public void debug(String string) {
        System.out.println("debug: " + string);
    }

    /*
     * Abaixo métodos de mineração.
     */
    private void minerIssues(List<Issue> gitIssues, Repository gitRepo) throws Exception {
        int i = 0;
        calculeSubProgress(i, gitIssues.size());
        while (!canceled && i < gitIssues.size()) {
            Issue gitIssue = gitIssues.get(i);
            EntityIssue issue = minerIssue(gitIssue);
            if (minerCommentsOfIssues && issue != null && issue.getCommentsCount() > 0) {
                minerCommentsOfIssue(issue, gitRepo);
            }
            EntityPullRequest pull = PullRequestServices.getPullRequestByNumber(gitIssue.getNumber(), repositoryToMiner, dao);
            if (pull != null) {
                pull.setIssue(issue);
                issue.setPullRequest(pull);
            }
            repositoryToMiner.addIssue(issue);
            dao.edit(issue);
            i++;
            calculeSubProgress(i, gitIssues.size());
        }
    }

    private EntityIssue minerIssue(Issue gitIssue) {
        EntityIssue issue = null;
        try {
            issue = IssueServices.createEntity(gitIssue, dao);
            out.printLog("Isseu gravada com sucesso: " + gitIssue.getTitle() + " - ID: " + gitIssue.getId());
        } catch (Exception ex) {
            ex.printStackTrace();
            out.printLog("## Erro ao gravar Issue: " + gitIssue.getTitle() + " - ID: " + gitIssue.getId() + " Descrição: " + ex.toString());
        }
        return issue;
    }

    private void minerCommentsOfIssue(EntityIssue issue, Repository gitRepo) throws Exception {
        out.printLog("");
        out.printLog("Baixando Comentários...");
        List<Comment> gitComments = new IssueService().getComments(gitRepo, issue.getNumber());
        out.printLog(gitComments.size() + " Comentários baixados.");
        int i = 0;
        while (!canceled && i < gitComments.size()) {
            Comment gitComment = gitComments.get(i);
            EntityComment comment = minerCommentOfIssue(gitComment);
            issue.addComment(comment);
            dao.edit(comment);
            i++;
        }
    }

    private EntityComment minerCommentOfIssue(Comment gitComment) {
        EntityComment comment = null;
        try {
            comment = CommentServices.createEntity(gitComment, dao);
            out.printLog("Comment gravado com sucesso: " + gitComment.getUrl() + " - ID: " + gitComment.getId());
            return comment;
        } catch (Exception ex) {
            ex.printStackTrace();
            out.printLog("## Erro ao gravar Comment: " + gitComment.getUrl() + " - ID: " + gitComment.getId() + " Descrição: " + ex.toString());
        }
        return comment;
    }

    private void minerCollaborators(List<User> collaborators) {
        int i = 0;
        calculeSubProgress(i, collaborators.size());
        while (!canceled && i < collaborators.size()) {
            User gitCollab = collaborators.get(i);
            EntityUser colab = minerCollaborator(gitCollab);
            colab.addCollaboratedRepository(repositoryToMiner);
            dao.edit(colab);
            i++;
            calculeSubProgress(i, collaborators.size());
        }
    }

    private EntityUser minerCollaborator(User gitCollab) {
        EntityUser colab = null;
        try {
            colab = UserServices.createEntity(gitCollab, dao, false);
            out.printLog("Collaborator gravado com sucesso: " + gitCollab.getLogin() + " - ID: " + gitCollab.getId());
        } catch (Exception ex) {
            ex.printStackTrace();
            out.printLog("## Erro ao gravar Collaborator: " + gitCollab.getLogin() + " - ID: " + gitCollab.getId() + " Descrição: " + ex.toString());
        }
        return colab;
    }

    private void minerWatchers(List<User> watchers) {
        int i = 0;
        calculeSubProgress(i, watchers.size());
        while (!canceled && i < watchers.size()) {
            User gitWatcher = watchers.get(i);
            EntityUser watcher = minerWatcher(gitWatcher);
            watcher.addWatchedRepository(repositoryToMiner);
            dao.edit(watcher);
            i++;
            calculeSubProgress(i, watchers.size());
        }
    }

    private EntityUser minerWatcher(User gitWatcher) {
        EntityUser watcher = null;
        try {
            watcher = UserServices.createEntity(gitWatcher, dao, false);
            out.printLog("Watcher gravado com sucesso: " + gitWatcher.getLogin() + " - ID: " + gitWatcher.getId());
        } catch (Exception ex) {
            ex.printStackTrace();
            out.printLog("## Erro ao gravar Watcher: " + gitWatcher.getLogin() + " - ID: " + gitWatcher.getId() + " Descrição: " + ex.toString());
        }
        return watcher;
    }

    private void minerPullRequests(List<PullRequest> gitPullRequests) throws Exception {
        int i = 0;
        calculeSubProgress(i, gitPullRequests.size());
        while (!canceled && i < gitPullRequests.size()) {
            PullRequest gitPull = gitPullRequests.get(i);
            EntityPullRequest pullRequest = minerPullRequest(gitPull);
            EntityIssue issue = IssueServices.getIssueByNumber(gitPull.getNumber(), repositoryToMiner, dao);
            if (issue != null) {
                issue.setPullRequest(pullRequest);
                pullRequest.setIssue(issue);
                dao.edit(issue);
            }
            repositoryToMiner.addPullRequest(pullRequest);
            dao.edit(pullRequest);
            i++;
            calculeSubProgress(i, gitPullRequests.size());
        }
    }

    private EntityPullRequest minerPullRequest(PullRequest gitPull) {
        EntityPullRequest pull = null;
        try {
            pull = PullRequestServices.createEntity(gitPull, dao);
            out.printLog("PullRequest gravado com sucesso: " + gitPull.getTitle() + " - ID: " + gitPull.getId());
        } catch (Exception ex) {
            ex.printStackTrace();
            out.printLog("## Erro ao gravar Issue: " + gitPull.getTitle() + " - ID: " + gitPull.getId() + " Descrição: " + ex.toString());
        }
        return pull;
    }

    private void minerForks(List<Repository> gitForks) {
        int i = 0;
        calculeSubProgress(i, gitForks.size());
        while (!canceled && i < gitForks.size()) {
            Repository gitFork = gitForks.get(i);
            EntityRepository fork = minerFork(gitFork);
            repositoryToMiner.addFork(fork);
            dao.edit(fork);
            i++;
            calculeSubProgress(i, gitForks.size());
        }
    }

    private EntityRepository minerFork(Repository gitFork) {
        EntityRepository fork = null;
        try {
            fork = RepositoryServices.createEntity(gitFork, dao, false);
            out.printLog("Fork gravado com sucesso: " + gitFork.getName() + " - ID: " + gitFork.getId());
        } catch (Exception ex) {
            ex.printStackTrace();
            out.printLog("## Erro ao gravar Fork: " + gitFork.getName() + " - ID: " + gitFork.getId() + " Descrição: " + ex.toString());
        }
        return fork;
    }

    private void minerRepositoryCommits(List<RepositoryCommit> gitRepoCommits, Repository gitRepo) throws Exception {
        int i = 0;
        calculeSubProgress(i, gitRepoCommits.size());
        while (!canceled && i < gitRepoCommits.size()) {
            RepositoryCommit gitRepoCommit = gitRepoCommits.get(i);
            EntityRepositoryCommit repoCommit = minerRepositoryCommit(gitRepoCommit);
            if (minerCommentsOfRepositoryCommits && repoCommit != null) {
                minerCommentsOfRepoCommit(repoCommit, gitRepo);
            }
            repositoryToMiner.addRepoCommit(repoCommit);
            dao.edit(repoCommit);
            i++;
            calculeSubProgress(i, gitRepoCommits.size());
        }
    }

    private EntityRepositoryCommit minerRepositoryCommit(RepositoryCommit gitRepoCommit) {
        EntityRepositoryCommit repoCommit = null;
        try {
            repoCommit = RepositoryCommitServices.createEntity(gitRepoCommit, dao);
            out.printLog("RepositoryCommit gravado com sucesso: " + gitRepoCommit.getUrl());
        } catch (Exception ex) {
            ex.printStackTrace();
            out.printLog("## Erro ao gravar RepositoryCommit: " + gitRepoCommit.getUrl() + " Descrição: " + ex.toString());
        }
        return repoCommit;
    }

    private void minerCommentsOfRepoCommit(EntityRepositoryCommit repoCommit, Repository gitRepo) throws Exception {
        out.printLog("");
        out.printLog("Baixando Comentários dos Commits...");
        List<CommitComment> gitCommitComments = new CommitService().getComments(gitRepo, repoCommit.getSha());
        out.printLog(gitCommitComments.size() + " Comentários baixados.");
        int i = 0;
        while (!canceled && i < gitCommitComments.size()) {
            CommitComment gitCommitComment = gitCommitComments.get(i);
            EntityCommitComment commitComment = minerCommentOfRepoCommit(gitCommitComment);
            repoCommit.getCommit().addComment(commitComment);
            dao.edit(commitComment);
            i++;
        }
    }

    private EntityCommitComment minerCommentOfRepoCommit(CommitComment gitCommitComment) {
        EntityCommitComment commitComment = null;
        try {
            commitComment = CommitCommentServices.createEntity(gitCommitComment, dao);
            out.printLog("Comment do Commit gravado com sucesso: " + gitCommitComment.getUrl());
        } catch (Exception ex) {
            ex.printStackTrace();
            out.printLog("## Erro ao gravar Comment do Commit: " + gitCommitComment.getUrl() + " Descrição: " + ex.toString());
        }
        return commitComment;
    }

    private void minerTeams(List<Team> gitTeams, Repository gitRepo) {
        int i = 0;
        calculeSubProgress(i, gitTeams.size());
        while (!canceled && i < gitTeams.size()) {
            Team gitTeam = gitTeams.get(i);
            EntityTeam team = minerTeam(gitTeam);
            repositoryToMiner.addTeam(team);
            dao.edit(team);
            i++;
            calculeSubProgress(i, gitTeams.size());
        }
    }

    private EntityTeam minerTeam(Team gitTeam) {
        EntityTeam team = null;
        try {
            team = TeamServices.createEntity(gitTeam, dao);
            out.printLog("Team gravado com sucesso: " + gitTeam.getName() + " - ID: " + gitTeam.getId());
        } catch (Exception ex) {
            ex.printStackTrace();
            out.printLog("## Erro ao gravar Team: " + gitTeam.getName() + " - ID: " + gitTeam.getId() + " Descrição: " + ex.toString());
        }
        return team;
    }
}