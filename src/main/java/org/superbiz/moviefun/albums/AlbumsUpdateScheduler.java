package org.superbiz.moviefun.albums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Configuration
@EnableAsync
@EnableScheduling
public class AlbumsUpdateScheduler {

    private static final long SECONDS = 1000;
    private static final long MINUTES = 60 * SECONDS;

    private final AlbumsUpdater albumsUpdater;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DataSource moviesDataSource;
    private final JdbcTemplate moviesJdbcTemplate;

    private static boolean doAlbumsUpdate = false;

    public AlbumsUpdateScheduler(AlbumsUpdater albumsUpdater, DataSource moviesDataSource) {
        this.albumsUpdater = albumsUpdater;
        this.moviesDataSource = moviesDataSource;
        moviesJdbcTemplate = new JdbcTemplate(moviesDataSource);
    }


    @Scheduled(initialDelay = 15 * SECONDS, fixedRate = 2 * MINUTES)
    public void run() {
        try {

            if (!doAlbumsUpdate) {
                int updatedRows = moviesJdbcTemplate.update(
                        "UPDATE album_scheduler_task" +
                                " SET started_at = now()" +
                                " WHERE started_at IS NULL" +
                                " OR started_at < date_sub(now(), INTERVAL 2 MINUTE)"
                );

                if (updatedRows > 0) {
                    doAlbumsUpdate = true;
                }
            }

            if (doAlbumsUpdate) {
                logger.debug("Starting albums update");
                albumsUpdater.update();
                doAlbumsUpdate = false;
                logger.debug("Finished albums update");
            }

        } catch (Throwable e) {
            logger.error("Error while updating albums", e);
        }
    }
}
