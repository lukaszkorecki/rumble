.PHONY: test update-deps release clean jar publish install help


update-deps:
	clj -M:dev/outdated


ifneq ($(SNAPSHOT),)
snapshot := :snapshot $(SNAPSHOT)
endif

clean:
	clj -T:build clean

jar:
	clj -T:build jar  $(snapshot)


publish:
	clj -T:build publish $(snapshot)

release: clean jar publish


install:
	clj -T:build install $(snapshot)


help:
	@echo $(MAKEFILE_LIST)
	@echo "Available commands:"
	@grep -E '^[a-zA-Z_-]+:.*' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*? "}; {printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2}'
