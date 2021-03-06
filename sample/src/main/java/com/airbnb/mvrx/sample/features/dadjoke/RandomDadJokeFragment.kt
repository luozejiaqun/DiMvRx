package com.airbnb.mvrx.sample.features.dadjoke

import com.airbnb.mvrx.*
import com.airbnb.mvrx.sample.core.BaseFragment
import com.airbnb.mvrx.sample.core.MvRxViewModel
import com.airbnb.mvrx.sample.core.simpleController
import com.airbnb.mvrx.sample.models.Joke
import com.airbnb.mvrx.sample.network.DadJokeService
import com.airbnb.mvrx.sample.views.basicRow
import com.airbnb.mvrx.sample.views.loadingRow
import com.airbnb.mvrx.sample.views.marquee
import javax.inject.Inject

data class RandomDadJokeState(val joke: Async<Joke> = Uninitialized) : MvRxState

class RandomDadJokeViewModel @Inject constructor(
    private val dadJokeService: DadJokeService
) : MvRxViewModel<RandomDadJokeState>(RandomDadJokeState()) {
    init {
        fetchRandomJoke()
    }

    fun fetchRandomJoke() {
        dadJokeService.random().execute { copy(joke = it) }
    }
}

class RandomDadJokeFragment : BaseFragment() {
    private val viewModel: RandomDadJokeViewModel by fragmentViewModel()

    override fun epoxyController() = simpleController(viewModel) { state ->
        marquee {
            id("marquee")
            title("Dad Joke")
        }

        /**
         * Async overrides the invoke operator so we can just call it. It will return the value if
         * it is Success or null otherwise.
         */
        val joke = state.joke()
        if (joke == null) {
            loadingRow {
                id("loading")
            }
            return@simpleController
        }

        basicRow {
            id("joke")
            title(joke.joke)
            clickListener { _ -> viewModel.fetchRandomJoke() }
        }
    }
}