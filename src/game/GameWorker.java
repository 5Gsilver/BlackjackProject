package game;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

public class GameWorker extends SwingWorker<Void, String> {

    public enum WorkType {
        DEAL_CARDS,
        DEALER_TURN
    }

    public interface GameAction {

        void execute() throws Exception;
    }

    public interface GameListener {

        void onProgress(String message);

        void onFinished();

        void onError(Exception e);
    }

    private final WorkType workType;
    private final GameAction gameAction;
    private final GameListener gameListener;
    private final int delay;

    public GameWorker(
            WorkType workType,
            GameAction gameAction,
            GameListener gameListener,
            int delay) {

        if (workType == null) {
            throw new IllegalArgumentException(
                    "작업 종류가 지정되지 않았습니다.");
        }

        if (gameAction == null) {
            throw new IllegalArgumentException(
                    "게임 작업이 지정되지 않았습니다.");
        }

        if (gameListener == null) {
            throw new IllegalArgumentException(
                    "GUI 리스너가 지정되지 않았습니다.");
        }

        if (delay < 0) {
            throw new IllegalArgumentException(
                    "지연 시간은 0 이상이어야 합니다.");
        }

        this.workType = workType;
        this.gameAction = gameAction;
        this.gameListener = gameListener;
        this.delay = delay;
    }

    @Override
    protected Void doInBackground() throws Exception {

        if (workType == WorkType.DEAL_CARDS) {

            publish("카드 배분을 시작합니다.");

        } else if (workType == WorkType.DEALER_TURN) {

            publish("딜러의 차례가 시작되었습니다.");
        }

        checkCancelled();

        gameAction.execute();

        checkCancelled();

        publish("게임 작업이 완료되었습니다.");

        return null;
    }

    @Override
    protected void process(List<String> chunks) {

        for (int i = 0; i < chunks.size(); i++) {

            String message = chunks.get(i);

            gameListener.onProgress(message);
        }
    }

    @Override
    protected void done() {

        try {

            if (isCancelled()) {
                gameListener.onProgress(
                        "게임 작업이 취소되었습니다.");
                return;
            }

            get();

            gameListener.onFinished();

        } catch (CancellationException e) {

            gameListener.onProgress(
                    "게임 작업이 취소되었습니다.");

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            gameListener.onError(e);

        } catch (ExecutionException e) {

            Throwable cause = e.getCause();

            if (cause instanceof Exception) {

                gameListener.onError((Exception) cause);

            } else {

                gameListener.onError(
                        new Exception("게임 작업 중 오류가 발생했습니다.", cause));
            }
        }
    }

    public void waitBetweenCards() throws InterruptedException {

        checkCancelled();

        Thread.sleep(delay);

        checkCancelled();
    }

    private void checkCancelled() throws InterruptedException {

        if (isCancelled() || Thread.currentThread().isInterrupted()) {

            throw new InterruptedException(
                    "게임 작업이 취소되었습니다.");
        }
    }

    public WorkType getWorkType() {

        return workType;
    }
}