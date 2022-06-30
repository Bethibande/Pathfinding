package de.bethibande.test;

public class Renderer extends Thread {

    private boolean render = true;
    private int fps_cap = 30;

    private int currentFPS = 0;

    private Runnable renderFunc;


    public void setRenderFunc(Runnable renderFunc) {
        this.renderFunc = renderFunc;
    }

    public boolean getRenderFunc() {
        return render;
    }

    public int getFps_cap() {
        return fps_cap;
    }

    public void setRender(boolean render) {
        this.render = render;
    }

    public int getCurrentFPS() {
        return currentFPS;
    }

    public void setFps_cap(int fps_cap) {
        this.fps_cap = fps_cap;
    }

    @Override
    public void run() {
        while(render) {
            long start = System.currentTimeMillis();

            renderFunc.run();

            try {
                long current = System.currentTimeMillis();
                long wait = (1000 / fps_cap) - (current - start);

                if(wait > 0) Thread.sleep(wait);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }

            long end = System.currentTimeMillis();
            currentFPS = 1000 / (int)(end - start);
        }
    }
}
