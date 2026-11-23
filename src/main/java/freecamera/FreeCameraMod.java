package freecamera;

import arc.Core;
import arc.Events;
import arc.input.KeyBind;
import arc.input.KeyCode;
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.game.EventType.WorldLoadEvent;
import mindustry.input.DesktopInput;
import mindustry.mod.Mod;
import mindustry.gen.Unit;

/** Desktop-only free camera. No unit teleportation or velocity edits. */
public class FreeCameraMod extends Mod{
    private static final String SPEED = "free-camera-speed";
    private static final KeyBind RETURN = KeyBind.add("free-camera-return", KeyCode.home, "view");
    private FreeCameraInput input;

    public FreeCameraMod(){
        Events.on(ClientLoadEvent.class, event -> Core.app.post(() -> {
            if(Vars.mobile) return;
            Core.settings.defaults(SPEED, 200);
            Vars.ui.settings.game.sliderPref(SPEED, 200, 50, 5000, 50, v -> v + "%");
            input = new FreeCameraInput();
            Vars.control.setInput(input);
        }));
        Events.on(WorldLoadEvent.class, event -> {
            if(input != null) input.reattach();
        });
    }

    public static class FreeCameraInput extends DesktopInput{
        private boolean detached;
        private float cameraX, cameraY;

        private boolean altHeld(){
            return Core.input.keyDown(KeyCode.altLeft) || Core.input.keyDown(KeyCode.altRight);
        }

        private boolean typing(){
            return Core.scene != null && (Core.scene.hasField() || Core.scene.hasDialog());
        }

        public void reattach(){
            detached = false;
            panning = false;
            spectating = null;
            if(Vars.player != null) Core.camera.position.set(Vars.player.x, Vars.player.y);
        }

        @Override
        public void update(){
            boolean active = Vars.state.isGame() && !typing();
            boolean alt = active && altHeld();
            if(active && Core.input.keyTap(RETURN)) reattach();
            if(alt && !detached){
                detached = true;
                cameraX = Core.camera.position.x;
                cameraY = Core.camera.position.y;
            }
            // Prevent the vanilla camera-follow branch from affecting camera coordinates.
            if(detached) panning = true;
            super.update();
            if(detached && Vars.state.isGame()){
                // Restore our camera position after the original input update.
                float dx = 0f, dy = 0f;
                if(alt){
                    if(Core.input.keyDown(KeyCode.a)) dx--;
                    if(Core.input.keyDown(KeyCode.d)) dx++;
                    if(Core.input.keyDown(KeyCode.w)) dy++;
                    if(Core.input.keyDown(KeyCode.s)) dy--;
                }
                if(dx != 0 && dy != 0){ dx *= 0.70710678f; dy *= 0.70710678f; }
                float speed = 5f * Core.settings.getInt(SPEED, 200) / 100f;
                cameraX += dx * speed * Math.min(Time.delta, 3f);
                cameraY += dy * speed * Math.min(Time.delta, 3f);
                Core.camera.position.set(cameraX, cameraY);
            }
        }

        @Override
        protected void updateMovement(Unit unit){
            // Do not call vanilla movement while Alt is held. Crucially, do not
            // rewrite unit position/velocity: the server remains authoritative.
            if(altHeld() && !typing()) return;
            super.updateMovement(unit);
        }
    }
}
