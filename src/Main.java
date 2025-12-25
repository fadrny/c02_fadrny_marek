import controller.Controller2D;
import view.Window;

void main() {
    Window window = new Window();
    new Controller2D(window.getPanel());
}
