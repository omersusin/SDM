package grab.bit.shared.ui.widget.sort

interface ComparatorProvider<T> {
    fun comparator(): Comparator<T>
}
