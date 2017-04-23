package momo.dao;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import module.processor.model.Sentence;

/**
 * Created by ercan on 09.04.2017.
 */
public class SentenceDAO {
    private String keyspace;
    private String tableName;
    private Session session;
    private PreparedStatement preparedStatement;

    public SentenceDAO(String keyspace, String tableName){
        this.keyspace = keyspace;
        this.tableName = tableName;
        session = createSession();
    }

    public void insert(Sentence sentence){
        BoundStatement bound = preparedStatement.bind(sentence.getOriginalSentence(),
                sentence.getQuestions(), sentence.getSourceName(),
                sentence.getStemmedWordsList(), sentence.getTags());
        session.execute(bound);
    }

    public void update(Sentence sentence){
        BoundStatement bound = preparedStatement.bind(sentence.getQuestions(),
                sentence.getSourceName(), sentence.getStemmedWordsList(),
                sentence.getTags());
        session.execute(bound);
    }

    public void delete(Sentence sentence){
        BoundStatement bound = preparedStatement.bind(sentence.getOriginalSentence());
        session.execute(bound);
    }

    public void prepareForInsert(){
        preparedStatement = session.prepare(
                "INSERT INTO " + tableName + " (original_sentence, questions, " +
                        "source_name, stemmed_words_list, tags) values (?, ?, ?, ?, ?)");
    }

    public void prepareForUpdate(){
        preparedStatement = session.prepare("UPDATE " + tableName +" " +
                "SET questions= ?, source_name=?, stemmed_words_list=?, tags=?" +
                "WHERE original_sentence=?");
    }

    public void prepareForDelete(){
        preparedStatement = session.prepare(
                "DELETE FROM " + tableName + " WHERE original_sentence=?");
    }

    private Session createSession(){
        Cluster.Builder clusterBuilder = Cluster.builder();
        Cluster cluster = clusterBuilder.addContactPoint("127.0.0.1").build();

        return cluster.connect(keyspace);
    }
}